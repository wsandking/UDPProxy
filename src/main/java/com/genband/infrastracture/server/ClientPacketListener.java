package com.genband.infrastracture.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.genband.infrastracture.management.UDPHandlerExecutorPool;

public class ClientPacketListener implements Runnable {

  private DatagramSocket listener;
  private byte[] buffer;

  public ClientPacketListener(DatagramSocket socket, int bufferSize) {

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
         * Send packets to Client handler
         */
        System.out.println("Receive client packet, about to process it");
        UDPHandlerExecutorPool.getInstance().processClientPackets(packet);

      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

}
