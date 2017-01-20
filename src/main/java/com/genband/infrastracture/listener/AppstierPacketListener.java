package com.genband.infrastracture.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import com.genband.infrastracture.handlers.AsPacketHandler;
import com.genband.infrastracture.handlers.TmpSocketHandler;
import com.genband.infrastracture.management.UDPExecutorPool;

public class AppstierPacketListener implements Runnable {

  private DatagramSocket listener;
  private byte[] buffer;
  private boolean running = false;

  private static Logger log = Logger.getLogger(AppstierPacketListener.class.getName());

  public AppstierPacketListener(DatagramSocket socket, int bufferSize) {

    log.info("UDP Buffer Size: " + bufferSize);
    this.listener = socket;
    buffer = new byte[bufferSize];
    this.running = true;

  }

  public void closeListener() {
    this.running = false;
  }

  @Override
  public void run() {

    while (running) {

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
          TmpSocketHandler.setAppstierPort(packet.getPort());

        }

        /**
         * Send packets to Client handler
         */
        UDPExecutorPool.getInstance().processClientPackets(packet);

      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

}
