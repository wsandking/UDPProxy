package com.genband.infrastracture.management;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.genband.infrastracture.handlers.AsPacketHandler;
import com.genband.infrastracture.handlers.ClientPacketHandler;
import com.genband.infrastracture.handlers.TmpSocketHandler;

/**
 * Manage the threads that handles incoming packets
 * 
 * @author sewang
 *
 */
public class UDPExecutorPool {

  private static UDPExecutorPool instance;
  private ExecutorService clientHandlersPool;
  private ExecutorService asHandlersPool;
  private ExecutorService tmpSocketListenPool;

  public static UDPExecutorPool getInstance() {

    if (null == instance)
      instance = new UDPExecutorPool();
    return instance;

  }

  private UDPExecutorPool() {

    clientHandlersPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(ClientPacketHandler.getType()).build());
    asHandlersPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(AsPacketHandler.getType()).build());
    tmpSocketListenPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(TmpSocketHandler.getType()).build());

  }

  public void processClientPackets(DatagramPacket packet) {

    /**
     * May use execute, need to investigate
     */
    clientHandlersPool.submit(new ClientPacketHandler().processPackets(packet));

  }

  public void processAsPackets(DatagramPacket packet) {

    /**
     * May use execute, need to investigate
     */
    asHandlersPool.submit(new AsPacketHandler().processPackets(packet));

  }

  public void setupTempSocketListener(DatagramSocket socket) {

    tmpSocketListenPool.submit(new TmpSocketHandler().setListenSocket(socket));

  }

}
