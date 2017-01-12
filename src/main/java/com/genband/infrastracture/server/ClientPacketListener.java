package com.genband.infrastracture.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import com.genband.infrastracture.handlers.AsPacketHandler;
import com.genband.infrastracture.management.UDPHandlerExecutorPool;

public class ClientPacketListener implements Runnable {

  private DatagramSocket listener;
  private byte[] buffer;

  private static Logger log = Logger.getLogger(ClientPacketListener.class.getName());

  public ClientPacketListener(DatagramSocket socket, int bufferSize) {
    log.info("UDP Buffer Size: " + bufferSize);
    this.listener = socket;
    buffer = new byte[bufferSize];

  }

  @Override
  public void run() {

    while (true) {

      try {

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        listener.receive(packet);

        /**
         * Test part
         */
        log.info(String.format("receive packets from appstier %s:%s",
            packet.getAddress().toString(), packet.getPort()));

        if (!AsPacketHandler.getAppstierPort().equals(packet.getPort())) {

          log.info("Update appstier port.");
          AsPacketHandler.setAppstierPort(packet.getPort());

        }

        /**
         * Send packets to Client handler
         */
        UDPHandlerExecutorPool.getInstance().processClientPackets(packet);

      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

}
