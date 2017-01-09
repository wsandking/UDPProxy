package com.genband.infrastracture.handlers;

import java.net.DatagramPacket;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;


public class AsPacketHandler implements PacketHandler {

  private static Logger log = Logger.getLogger(AsPacketHandler.class.getName());
  private static final String HANDLER_TYPE = "AS Application Handler";
  private static Integer udpPacketSize;


  static {
  }

  private DatagramPacket packet;

  @Override
  public AsPacketHandler processPackets(DatagramPacket packet) {
    // TODO Auto-generated method stub
    this.packet = packet;
    return this;
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
