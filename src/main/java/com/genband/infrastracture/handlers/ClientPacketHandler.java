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

  private static DatagramSocket testSocket;


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
        System.out.println("Username: " + username);

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

    System.out.println("About to send out message");
    
    if (testSocket == null){
      
      System.out.println("Initialize sockets");
      testSocket = new DatagramSocket(ad.getPort(), InetAddress.getByName(ad.getIpAddress()));
      
    }
    testSocket.send(udppack);

    // DatagramSocket ds = new DatagramSocket(ad.getPort(),
    // InetAddress.getByName(ad.getIpAddress()));
    // ds.send(udppack);

    // ds.close();
    /**
     * For testing on purpose on windows, open a socket re-route it to our listening port
     */
    // testReceivePacket(ds);
    testReceivePacket(testSocket);
  }

  private void testReceivePacket(DatagramSocket ds) {
    // TODO Auto-generated method stub

    try {
      byte buffer[] = new byte[1450];
      DatagramPacket pac = new DatagramPacket(buffer, buffer.length);
      ds.receive(pac);

      pac.setPort(packet.getPort());
      System.out.print(packet.getPort());

      pac.setAddress(packet.getAddress());
      System.out.print(packet.getAddress());

      ds.send(pac);

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

}
