package com.genband.infrastracture.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.genband.infrastracture.handlers.AsPacketHandler;
import com.genband.infrastracture.management.UDPHandlerExecutorPool;

public class AsPacketListener implements Runnable {

  private DatagramSocket listener;
  private byte[] buffer;

  public AsPacketListener(DatagramSocket socket, DatagramSocket sender, int bufferSize) {

    this.listener = socket;
    buffer = new byte[bufferSize];
    AsPacketHandler.setSenderSocket(sender);

  }

  @Override
  public void run() {

    while (true) {

      try {

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        listener.receive(packet);
        UDPHandlerExecutorPool.getInstance().processAsPackets(packet);

      } catch (IOException e) {

        e.printStackTrace();
      }

    }

  }

}
