package com.genband.infrastracture.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.exception.AddressException;
import com.genband.infrastracture.hazelcast.UDPProxyHazelCastServer;
import com.genband.infrastracture.management.Address;
import com.genband.infrastracture.management.AddressAllocator;
import com.genband.infrastracture.management.UDPExecutorPool;

/**
 * Do not for trying.
 * 
 * @author sewang
 *
 */
public class ClientPacketHandler implements PacketHandler {

  private static final String HANDLER_TYPE = "Client Packet Handler";
  private static final String OK_STATUS_FOR_LOGIN = "SIP/2.0 200 OK";
  private static final String TRYING = "SIP/2.0 100 Trying";
  private static final String UDP_USER_FROM =
      "From: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";
  private static final String UDP_USER_TO =
      "To: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";
  private static final String UDP_CONTACT =
      "Contact: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";

  /**
   * MapMaker, socket will have time to live, test for 1 minutes
   * Can I define any destruction rule
   */
  private static Map<String, DatagramSocket> socketPool;

  private static Logger log = Logger.getLogger(ClientPacketHandler.class.getName());
  private DatagramPacket packet;

  private static String asServerAddress;
  private static Integer asServerPort;
  private static Pattern fromUserP;
  private static Pattern toUserP;
  private Matcher fromPatternMap;
  private Matcher toPatternMap;

  /**
   * Should both be removed
   */
  // private static DatagramSocket testSocket;
  // private static DatagramSocket presTierSocket;
  // private static boolean openListener = false;

  static {

    asServerAddress = ConfigurationManager.getInstance().getAsServerAddress();
    asServerPort = ConfigurationManager.getInstance().getAsServerPort();
    fromUserP = Pattern.compile(UDP_USER_FROM);
    toUserP = Pattern.compile(UDP_USER_TO);
    socketPool = new HashMap<String, DatagramSocket>();

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

      /**
       * Have already got the message of the whole packet here, may just check
       */
      String content = new String(packet.getData(), 0, packet.getLength());
      /**
       * Check if message is trying, if it is trying do not send anything.
       */
      if (!content.contains(TRYING)) {
        fromPatternMap = fromUserP.matcher(content);

        String fromUsername = null;
        String toUsername = null;

        if (fromPatternMap.find()) {
          // we're only looking for one group, so get it
          fromUsername = fromPatternMap.group(1) + "@" + fromPatternMap.group(2);

          /**
           * Find to username, may have, may not
           */
          toPatternMap = toUserP.matcher(content);
          if (toPatternMap.find()) {
            toUsername = toPatternMap.group(1) + "@" + toPatternMap.group(2);
          }


          Address ads = this.getUserContactInfo(fromUsername, toUsername);

          if (null == ads) {

            ads = AddressAllocator.getInstance().getAvailableAddress();
            this.saveContactInfo(fromUsername, ads);

          }

          String address = ads.getIpAddress() + ":" + ads.getPort();

          log.info(String.format("Username: %s, Address: %s", fromUsername, ads.toString()));

          DatagramPacket dp = this.constructPackets(address, content);
          DatagramSocket ds = this.getSocket(ads, address);
          this.sendPackets(ds, dp);

          // ds.close();

          if (this.checkIfItShouldbeClosed(content)) {

            log.info("Tempo socket should be closed.");
            synchronized (socketPool) {
              socketPool.remove(address);
            }

            ds.close();

          }

        } else {

          log.info(String.format(" The filtered content: \n%s",
              new String(packet.getData(), 0, packet.getLength())));

        }
      } else {

        log.info("Trying message, filtering out. ");

      }
    } catch (AddressException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
      e.printStackTrace();
    }

  }

  private boolean checkIfItShouldbeClosed(String content) {
    // TODO Auto-generated method stub
    /**
     * Check if content-length is 0,
     * Check if message is bye.
     */
    boolean result = false;

    if (content.contains(OK_STATUS_FOR_LOGIN)) {
      log.info("***********Login successful, Socket should be close ***************");
      result = true;
    }
    return result;

  }

  private Address getUserContactInfo(String fromUsername, String toUsername) {

    Address address = null;

    if (null != UDPProxyHazelCastServer.getInstance()
        .getValueFromAddressMapByUsername(fromUsername))
      address =
          UDPProxyHazelCastServer.getInstance().getValueFromAddressMapByUsername(fromUsername);
    else if (null != UDPProxyHazelCastServer.getInstance()
        .getValueFromAddressMapByUsername(toUsername))
      address = UDPProxyHazelCastServer.getInstance().getValueFromAddressMapByUsername(toUsername);

    return address;

  }

  /**
   * If create socket too costly, probably just re-use some
   * 
   * @param ds
   * 
   * @param udppack
   * @param ad
   * @throws IOException
   */
  private void sendPackets(DatagramSocket ds, DatagramPacket udppack) throws IOException {
    // TODO Auto-generated method stub
    udppack.setAddress(InetAddress.getByName(asServerAddress));
    udppack.setPort(asServerPort);

    /**
     * The actually working code
     */
    ds.send(udppack);
    log.info("Message send to AS server - address: " + udppack.getAddress() + " port: "
        + udppack.getPort());


  }

  private DatagramSocket getSocket(Address ad, String address)
      throws SocketException, UnknownHostException {
    // TODO Auto-generated method stub
    DatagramSocket ds = null;

    // ds = new DatagramSocket(ad.getPort(), InetAddress.getByName(ad.getIpAddress()));

    synchronized (socketPool) {

      ds = socketPool.get(address);
      if (null == ds) {

        log.info("Creating a new Socket and put it Socket_Pool");
        ds = new DatagramSocket(ad.getPort(), InetAddress.getByName(ad.getIpAddress()));
        socketPool.put(address, ds);

        /**
         * Open a socket listener to listen
         */
        UDPExecutorPool.getInstance().setupTempSocketListener(ds);

      }

    }
    return ds;
  }

  // private void testReceivePacket(DatagramSocket ds) {
  // // TODO Auto-generated method stub
  //
  // try {
  // while (true) {
  //
  // byte buffer[] = new byte[3000];
  // DatagramPacket pac = new DatagramPacket(buffer, buffer.length);
  // ds.receive(pac);
  // log.info("Receive message from test socket listenning part... ");
  // pac.setPort(packet.getPort());
  // pac.setAddress(packet.getAddress());
  //
  // presTierSocket.send(pac);
  // log.info("Message send back to apps tier - address: " + packet.getAddress() + " port: "
  // + packet.getPort());
  // }
  // } catch (IOException e) {
  //
  // log.error("Message send back to apps tier - address: " + packet.getAddress() + " port: "
  // + packet.getPort());
  //
  // }
  //
  // }

  /**
   * To fix
   * 
   * @param username
   * @throws AddressException
   * @throws IOException
   */
  private DatagramPacket constructPackets(String address, String content)
      throws AddressException, IOException {

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
  // public static DatagramSocket getTestAnotherSocket() {
  // return presTierSocket;
  // }
  //
  // public static void setTestAnotherSocket(DatagramSocket testAnotherSocket) {
  // ClientPacketHandler.presTierSocket = testAnotherSocket;
  // }

}
