package com.genband.infrastracture.handlers;

import java.net.DatagramSocket;

/**
 * 
 * Reverse
 * 
 * @author sewang
 *
 */
public interface PacketHandler extends Runnable {

  public void processPackets(DatagramSocket socket);

}
