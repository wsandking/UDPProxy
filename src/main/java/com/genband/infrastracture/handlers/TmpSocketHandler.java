package com.genband.infrastracture.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import com.genband.infrastracture.config.ConfigurationManager;

public class TmpSocketHandler implements Runnable {

  private static final String UDP_CONTACT =
      "Contact: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)";

  private static String proxyAddress;
  private static int bufferSize;
  private static DatagramSocket appsTierSocket;
  private static Logger log = Logger.getLogger(TmpSocketHandler.class.getName());
  private static String[] appstierAddresses;
  private static Integer appstierPort;
  private static Integer index;

  private DatagramSocket listenSocket;
  private byte[] buffer;


  static {

    bufferSize = ConfigurationManager.getInstance().getAsBufferSize();
    appstierAddresses = ConfigurationManager.getInstance().getAppstierAddresses().split(";");
    appstierPort = ConfigurationManager.getInstance().getAsListenPort();
    proxyAddress = ConfigurationManager.getInstance().getClientSideIP() + ":"
        + ConfigurationManager.getInstance().getClientSidePort();

  }

  public TmpSocketHandler() {
    super();
    this.buffer = new byte[bufferSize];
  }

  public TmpSocketHandler setListenSocket(DatagramSocket listenSocket) {

    this.listenSocket = listenSocket;
    return this;

  }

  public static void setAppsTierSocket(DatagramSocket appsTierSocket) {
    
    TmpSocketHandler.appsTierSocket = appsTierSocket;
    
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    try {
      while (true) {

        DatagramPacket pac = new DatagramPacket(buffer, buffer.length);
        listenSocket.receive(pac);
        log.info("Receive message from temp socket pool... ");

        String destinationIp = null;
        if (appstierAddresses.length > 1 && index < appstierAddresses.length && index >= 0) {

          synchronized (index) {

            destinationIp = appstierAddresses[index];
            if (++index >= appstierAddresses.length)
              index = 0;

          }

        } else
          destinationIp = appstierAddresses[0];

        pac.setAddress(InetAddress.getByName(destinationIp));
        pac.setPort(appstierPort);

        this.constructPacket(pac);
        appsTierSocket.send(pac);
        log.info("Message send back to apps tier - address: " + pac.getAddress() + " port: "
            + pac.getPort());

      }
    } catch (IOException e) {

      log.error("Message cannot send back to apps tier ");

    }
  }

  public static void setAppstierPort(Integer appstierPort) {
    
    TmpSocketHandler.appstierPort = appstierPort;
  
  }

  private DatagramPacket constructPacket(DatagramPacket packet) {

    String content = new String(packet.getData(), 0, packet.getLength());
    String newStr = content.replaceAll(UDP_CONTACT, "Contact: <sip:$1@" + proxyAddress);

    byte[] newContent = newStr.getBytes();
    DatagramPacket udppack = new DatagramPacket(newContent, newContent.length);
    return udppack;

  }

}
