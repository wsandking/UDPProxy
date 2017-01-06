package com.genband.infrastracture.management;

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

  public static UDPHandlerExecutorPool getInstance() {

    if (null == instance)
      instance = new UDPHandlerExecutorPool();
    return instance;

  }

  private UDPHandlerExecutorPool() {

    clientHandlersPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(AsPacketHandler.getType()).build());
    asHandlersPool = Executors.newCachedThreadPool(new HandlerThreadFactory().setDaemon(false)
        .setNamePrefix(ClientPacketHandler.getType()).build());


  }



}
