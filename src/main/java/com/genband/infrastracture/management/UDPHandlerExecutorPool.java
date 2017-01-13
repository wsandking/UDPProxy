package com.genband.infrastracture.management;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.genband.infrastracture.handlers.AsPacketHandler;
import com.genband.infrastracture.handlers.ClientPacketHandler;

/**
 * Manage the threads that handles incoming packets
 * 
 * @author sewang
 *
 */
public class UDPHandlerExecutorPool {

  private static UDPHandlerExecutorPool instance;
  private ExecutorService clientHandlersPool;
  private ExecutorService asHandlersPool;
  private ExecutorService tmpSocketListenPool;

  public static UDPHandlerExecutorPool getInstance() {

    if (null == instance)
      instance = new UDPHandlerExecutorPool();
    return instance;

  }

  private UDPHandlerExecutorPool() {

    clientHandlersPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(ClientPacketHandler.getType()).build());
    asHandlersPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(AsPacketHandler.getType()).build());

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
    
    

  }

}
