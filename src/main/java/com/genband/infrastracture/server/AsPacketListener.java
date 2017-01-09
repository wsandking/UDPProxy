package com.genband.infrastracture.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AsPacketListener implements Runnable {

  private DatagramSocket listener;
  private byte[] buffer;

  public AsPacketListener(DatagramSocket socket, int bufferSize) {

    this.listener = socket;
    buffer = new byte[bufferSize];

  }

  @Override
  public void run() {

    while (true) {
      
      try {
        
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        listener.receive(packet);
        
        
      } catch (IOException e) {

        e.printStackTrace();
      }
      
    }

  }

}
