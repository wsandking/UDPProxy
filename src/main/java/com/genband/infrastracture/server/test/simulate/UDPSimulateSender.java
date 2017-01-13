package com.genband.infrastracture.server.test.simulate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPSimulateSender {

  private DatagramSocket socket;
  // private DatagramSocket senderSocket;
  private int clientPort;


  /**
   * Should be able to send something, and receive something
   * 
   * @throws UnknownHostException
   */
  public UDPSimulateSender() throws UnknownHostException {
    try {
      socket = new DatagramSocket(5096);
      // senderSocket = new DatagramSocket(5092, InetAddress.getByName("192.168.56.1"));
    } catch (SocketException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 
   */
  public void receivePacket() {
    while (true) {
      try {
        byte buffer[] = new byte[1450];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        int port = packet.getPort();

        String content = new String(packet.getData(), 0, packet.getLength());

        if (port != 5060) {
          clientPort = packet.getPort();
          /**
           * Print packet information out and forward to correct address
           */
          System.out.println("\nHost: " + packet.getAddress() + "\nPort: " + packet.getPort()
              + "\nLength: " + packet.getLength() + "\nData: " + content);
          /*
           * Domain replacement
           * 
           */
          String address = "172.28.248.8:9102";
          String newStr =
              content.replaceAll("Contact: <sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)",
                  "Contact: <sip:$1@" + address);
          byte[] newContent = newStr.getBytes();
          DatagramPacket udppack = new DatagramPacket(newContent, newContent.length);

          processPackets(udppack);

        } else {
          System.out.println("\nHost: " + packet.getAddress() + "\nPort: " + packet.getPort()
              + "\nLength: " + packet.getLength() + "\nData: " + content);
          processToClient(packet);
        }
      } catch (IOException ex) {

      }
    }
  }

  /**
   * 
   * @param packet
   */
  public void processPackets(DatagramPacket packet) {
    try {
      packet.setAddress(InetAddress.getByName("172.28.248.8"));
      packet.setPort(5060);

      socket.send(packet);
      System.out.println("Forward to SIP Server");



    } catch (IOException ex) {

    }
  }

  /**
   * 
   * @param packet
   */
  public void processToClient(DatagramPacket packet) {
    try {
      packet.setAddress(InetAddress.getByName("172.28.19.60"));
      packet.setPort(this.clientPort);

      socket.send(packet);
      System.out.println("Forward to SIP Client");
    } catch (IOException ex) {

    }
  }

  public static void main(String args[]) throws UnknownHostException, InterruptedException {
//    UDPSimulateSender sender = new UDPSimulateSender();
//
    String str = String.join("\n", "REGISTER sip:sidr.dev.genband.com SIP/2.0",
        "SIP/2.0 200 OK",
        "Via: SIP/2.0/UDP 172.28.19.60:5090;branch=z9hG4bK-524287-1---b3b3e77b860f4d13;rport",
        "Max-Forwards: 70",
        "Contact: \"testagent02\"<sip:testagent01@172.28.19.60:5090;rinstance=94b99f6f8d82633e>",
        "To: <sip:testagent01@spidr.dev.genband.com>",
        "From: \"testagent02\"<sip:testagent01@spidr.dev.genband.com>;tag=6b7e7072",
        "Call-ID: 83108ZDIxOGQ4YmM4NDUxN2FhYTdmYWUzYTUyYmEwZGNmNWQ", "CSeq: 1 REGISTER",
        "Expires: 3600",
        "Allow: SUBSCRIBE, NOTIFY, INVITE, ACK, CANCEL, BYE, REFER, INFO, OPTIONS, MESSAGE",
        "User-Agent: X-Lite release 4.9.7 stamp 83108", "Content-Length: 0");;
    System.out.println(str);
    System.out.println();
    String address = "172.28.248.8:9102";
    String newStr = str.replaceAll("Contact: [\"a-zA-Z0-9\\.\\: ]*<sip:([a-zA-Z0-9\\.\\:]+)@([a-zA-Z0-9\\.\\:]+)",
        "Contact: <sip:$1@" + address);

    System.out.println(newStr);
    System.out.println(newStr.contains("SIP/2.0 200 OK"));
//    sender.receivePacket();
//    String a = "T";
//    System.out.println(a.getBytes().length);
    
  }

}
