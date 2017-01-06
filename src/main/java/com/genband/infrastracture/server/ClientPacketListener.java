package com.genband.infrastracture.server;

import java.net.DatagramSocket;

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

    }
  }

}
