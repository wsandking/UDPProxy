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
        if (null == this.getUserContactInfo(username)) {
          this.processPacketsForNewUser(username);
        }
      }


    } catch (AddressException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }

  }

  private String getUserContactInfo(String username) {

    String address = null;
    if (null != UDPProxyHazelCastServer.getInstance().getValueFromAddressMapByUsername(username))
      address = UDPProxyHazelCastServer.getInstance().getValueFromAddressMapByUsername(username);
    return address;

  }

  private void sendPackets(DatagramPacket udppack, Address ad) throws IOException {
    // TODO Auto-generated method stub
    udppack.setAddress(InetAddress.getByName(asServerAddress));
    udppack.setPort(asServerPort);

    DatagramSocket ds = new DatagramSocket(ad.getPort(), InetAddress.getByName(ad.getIpAddress()));
    ds.send(udppack);

    /**
     * For testing on purpose on windows, open a socket re-route it to our listening port
     */
    testReceivePacket(ds);
  }

  private void testReceivePacket(DatagramSocket ds) {
    // TODO Auto-generated method stub

    try {
      byte buffer[] = new byte[1450];
      DatagramPacket pac = new DatagramPacket(buffer, buffer.length);
      ds.receive(pac);

      pac.setPort(packet.getPort());
      pac.setAddress(packet.getAddress());

      ds.send(pac);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }



  }

  /**
   * To fix
   * @param username
   * @throws AddressException
   * @throws IOException
   */
  private void processPacketsForNewUser(String username) throws AddressException, IOException {

    Address ad = AddressAllocator.getInstance().getAvailableAddress();
    String content = new String(packet.getData(), 0, packet.getLength());

    String address = ad.getIpAddress() + ":" + ad.getPort();

    String newStr = content.replaceAll(UDP_CONTACT, "Contact: <sip:$1@" + address);

    byte[] newContent = newStr.getBytes();
    DatagramPacket udppack = new DatagramPacket(newContent, newContent.length);

    if (null != username && null != address) {
      this.saveContactInfo(username, address);
      this.sendPackets(udppack, ad);
    }

  }

  private void saveContactInfo(String username, String address) {
    UDPProxyHazelCastServer.getInstance().addUsernameAddressMap(username, address);
  }

  public static String getType() {
    // TODO Auto-generated method stub
    return HANDLER_TYPE;
  }

}
