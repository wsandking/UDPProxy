package com.genband.infrastracture.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import com.genband.infrastracture.config.ConfigurationManager;

public class AsPacketHandler implements PacketHandler {

  private static Logger log = Logger.getLogger(AsPacketHandler.class.getName());
  private static final String HANDLER_TYPE = "AS Application Handler";
  private static final String UDP_CONTACT =
      "Contact: <sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";

  private static String[] appstierAddresses;
  private static String clientSideIP;
  private static Integer clientSidePort;
  private static Integer appstierPort;
  private static Integer index;
  private static DatagramSocket clientSideSocket;

  static {

    appstierAddresses = ConfigurationManager.getInstance().getAppstierAddresses().split(";");
    appstierPort = ConfigurationManager.getInstance().getAsListenPort();
    clientSideIP = ConfigurationManager.getInstance().getClientSideIP();
    clientSidePort = ConfigurationManager.getInstance().getClientSidePort();
    index = 0;

  }

  private DatagramPacket packet;

  @Override
  public AsPacketHandler processPackets(DatagramPacket packet) {
    // TODO Auto-generated method stub
    this.packet = packet;
    return this;
  }

  public static void setSenderSocket(DatagramSocket socket) {
    // TODO Auto-generated method stub
    clientSideSocket = socket;

  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    /**
     * Process packets, subs contact to same ip, port combination that facing client
     * 1. sub contact with ip/port combination of appstier facing socket
     * 2. find the appstier ip/port combination
     * 3. use client listening socket to send message
     */
    try {
      /**
       * Future may have multiple ip on apps-tier side
       */
      String address = clientSideIP + ":" + clientSidePort;
      String desIp = null;

      if (appstierAddresses.length > 1 && index < appstierAddresses.length && index >= 0) {

        synchronized (index) {

          desIp = appstierAddresses[index];
          if (++index >= appstierAddresses.length)
            index = 0;

        }

      } else
        desIp = appstierAddresses[0];

      DatagramPacket dp = this.constructPacket(address);
      this.sendPacket(dp, desIp);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }

  }

  private void sendPacket(DatagramPacket dp, String destinationIp) throws IOException {
    // TODO Auto-generated method stub
    dp.setAddress(InetAddress.getByName(destinationIp));
    dp.setPort(appstierPort);

    /**
     * 
     */
    log.info("Send message back to apps tier. " + " Address: " + destinationIp + ":" + appstierPort
        + " From Socket " + clientSideSocket.getLocalAddress().toString() + ":"
        + clientSideSocket.getPort());

    /***
     * For debug purpose print out the information
     */
    String contents = new String(dp.getData(), 0, dp.getLength());
    log.info("Contents: \n\n" + contents);

    clientSideSocket.send(dp);

  }

  private DatagramPacket constructPacket(String address) {
    // TODO Auto-generated method stub
    String content = new String(packet.getData(), 0, packet.getLength());
    String newStr = content.replaceAll(UDP_CONTACT, "Contact: <sip:$1@" + address);

    byte[] newContent = newStr.getBytes();
    DatagramPacket udppack = new DatagramPacket(newContent, newContent.length);
    return udppack;

  }

  public static String getType() {
    // TODO Auto-generated method stub
    return HANDLER_TYPE;
  }

  public static Integer getAppstierPort() {
    return appstierPort;
  }

  public static void setAppstierPort(Integer appstierPort) {
    AsPacketHandler.appstierPort = appstierPort;
  }



}
