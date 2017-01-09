package com.genband.infrastracture.handlers;

import java.net.DatagramPacket;


/**
 * 
 * Reverse
 * 
 * @author sewang
 *
 */
public interface PacketHandler extends Runnable {

  public PacketHandler processPackets(DatagramPacket packet);

}
