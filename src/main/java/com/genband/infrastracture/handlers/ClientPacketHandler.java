package com.genband.infrastracture.handlers;

import java.net.DatagramSocket;

public class ClientPacketHandler implements PacketHandler {

  private static final String HANDLER_TYPE = "Client Packet Handler";

  @Override
  public void processPackets(DatagramSocket socket) {
    // TODO Auto-generated method stub

  }

  @Override
  public void run() {
    // TODO Auto-generated method stub

  }

  public static String getType() {
    // TODO Auto-generated method stub
    return HANDLER_TYPE;
  }

}
