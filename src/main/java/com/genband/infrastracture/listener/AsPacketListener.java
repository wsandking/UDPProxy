package com.genband.infrastracture.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import com.genband.infrastracture.handlers.AsPacketHandler;
import com.genband.infrastracture.handlers.TmpSocketHandler;
import com.genband.infrastracture.management.UDPExecutorPool;

public class AsPacketListener implements Runnable {

  private DatagramSocket listener;
  private byte[] buffer;
  private boolean running = false;

  private static Logger log = Logger.getLogger(AsPacketListener.class.getName());

  public AsPacketListener(DatagramSocket socket, DatagramSocket sender, int bufferSize) {

    log.info("UDP Buffer Size: " + bufferSize);
    this.listener = socket;
    buffer = new byte[bufferSize];
    AsPacketHandler.setSenderSocket(sender);
    TmpSocketHandler.setAppsTierSocket(sender);
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

        log.info(String.format("receive packets from AS: %s:%s", packet.getAddress().toString(),
            packet.getPort()));

        UDPExecutorPool.getInstance().processAsPackets(packet);

      } catch (IOException e) {

        e.printStackTrace();
      }

    }

  }

}
