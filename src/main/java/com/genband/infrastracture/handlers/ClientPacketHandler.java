package com.genband.infrastracture.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.exception.AddressException;
import com.genband.infrastracture.hazelcast.UDPProxyHazelCastServer;
import com.genband.infrastracture.management.Address;
import com.genband.infrastracture.management.AddressAllocator;

public class ClientPacketHandler implements PacketHandler {

  private static final String HANDLER_TYPE = "Client Packet Handler";
  private static final String UDP_USER_FROM =
      "From: <sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";
  private static final String UDP_CONTACT =
      "Contact: <sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";

  private static Logger log = Logger.getLogger(ClientPacketHandler.class.getName());
  private DatagramPacket packet;

  private static String asServerAddress;
  private static Integer asServerPort;
  private static Pattern p;

  /**
   * Should both be removed
   */
  private static DatagramSocket testSocket;
  private static DatagramSocket testAnotherSocket;
  private static boolean openListener = false;

  static {
    asServerAddress = ConfigurationManager.getInstance().getAsServerAddress();
    asServerPort = ConfigurationManager.getInstance().getAsServerPort();
    p = Pattern.compile(UDP_USER_FROM);
  }


  @Override
  public ClientPacketHandler processPackets(DatagramPacket packet) {
    // TODO Auto-generated method stub
    this.packet = packet;
    return this;

  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    /**
     * At this moment open an port and listen
     */
    try {

      String content = new String(packet.getData(), 0, packet.getLength());
      Matcher m = p.matcher(content);
      String username = null;
      if (m.find()) {
        // we're only looking for one group, so get it
        username = m.group(1) + "@" + m.group(2);

        Address ads = this.getUserContactInfo(username);
        if (null == ads) {
          ads = AddressAllocator.getInstance().getAvailableAddress();
          this.saveContactInfo(username, ads);
        }

        DatagramPacket dp = this.constructPackets(ads);
        this.sendPackets(dp, ads);

      }

    } catch (AddressException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }

  }

  private Address getUserContactInfo(String username) {

    Address address = null;
    if (null != UDPProxyHazelCastServer.getInstance().getValueFromAddressMapByUsername(username))
      address = UDPProxyHazelCastServer.getInstance().getValueFromAddressMapByUsername(username);
    return address;

  }

  /**
   * If create socket too costly, probably just re-use some
   * 
   * @param udppack
   * @param ad
   * @throws IOException
   */
  private void sendPackets(DatagramPacket udppack, Address ad) throws IOException {
    // TODO Auto-generated method stub
    udppack.setAddress(InetAddress.getByName(asServerAddress));
    udppack.setPort(asServerPort);

    /**
     * The actually working code
     */
    // DatagramSocket ds = new DatagramSocket(ad.getPort(),
    // InetAddress.getByName(ad.getIpAddress()));
    // ds.send(udppack);
    // log.info("Message send to AS server - address: " + udppack.getAddress() + " port: "
    // + udppack.getPort());
    // ds.close();

    if (testSocket == null) {

      testSocket = new DatagramSocket(ad.getPort(), InetAddress.getByName(ad.getIpAddress()));

    }

    testSocket.send(udppack);
    log.info(
        "Test socket sent packets to " + udppack.getAddress().toString() + ":" + udppack.getPort());

    /**
     * For testing on purpose on windows, open a socket re-route it to our listening port
     */
    if (openListener == false)
      testReceivePacket(testSocket);

  }

  private void testReceivePacket(DatagramSocket ds) {
    // TODO Auto-generated method stub

    try {
      while (true) {
        byte buffer[] = new byte[3000];
        DatagramPacket pac = new DatagramPacket(buffer, buffer.length);
        ds.receive(pac);
        log.info("Receive message from test socket listenning part... ");
        pac.setPort(packet.getPort());
        pac.setAddress(packet.getAddress());

        /**
         * Fragmentation
         */
        
        
        testAnotherSocket.send(pac);
        log.info("Message send back to apps tier - address: " + packet.getAddress() + " port: "
            + packet.getPort());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * To fix
   * 
   * @param username
   * @throws AddressException
   * @throws IOException
   */
  private DatagramPacket constructPackets(Address ad) throws AddressException, IOException {

    String content = new String(packet.getData(), 0, packet.getLength());
    String address = ad.getIpAddress() + ":" + ad.getPort();
    String newStr = content.replaceAll(UDP_CONTACT, "Contact: <sip:$1@" + address);

    byte[] newContent = newStr.getBytes();
    DatagramPacket udppack = new DatagramPacket(newContent, newContent.length);

    return udppack;

  }

  private void saveContactInfo(String username, Address address) {
    UDPProxyHazelCastServer.getInstance().addUsernameAddressMap(username, address);
  }



  public static String getType() {
    // TODO Auto-generated method stub
    return HANDLER_TYPE;
  }

  /**
   * Test function, should be removed
   */
  public static DatagramSocket getTestAnotherSocket() {
    return testAnotherSocket;
  }

  public static void setTestAnotherSocket(DatagramSocket testAnotherSocket) {
    ClientPacketHandler.testAnotherSocket = testAnotherSocket;
  }

}
