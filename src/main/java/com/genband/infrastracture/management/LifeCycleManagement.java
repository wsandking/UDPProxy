package com.genband.infrastracture.management;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.listener.AppstierPacketListener;
import com.genband.infrastracture.listener.AsPacketListener;

/**
 * Listen for keepalived instruction, once it is activated, it will start working
 * 
 * @author sewang
 *
 */
public class LifeCycleManagement {

  /**
   * 
   * Heart beat,
   * Start,
   * 
   * 
   * Stop,
   * 
   */
  private static Logger log = Logger.getLogger(LifeCycleManagement.class);
  private static LifeCycleManagement instance;

  private ConfigurationManager configManager;
  private Thread appstierListenThread;
  private Thread asListenThread;
  private DatagramSocket clientSideSocket;
  private DatagramSocket asSocket;

  public static LifeCycleManagement getInstance() {

    if (null == instance) {
      instance = new LifeCycleManagement();
    }

    return instance;

  }

  private LifeCycleManagement() {

    /**
     * Something to initialize here
     */
    try {

      ServerSocket welcomeSocket = new ServerSocket(6789);

    } catch (IOException e) {

      log.error("Couldn't initialize listening port");

    }

  }

  public void startListenning() {

    /**
     * Start management tcp port to listenning
     */


  }

  public void stopServer() {



  }

  public void heartBeat() {



  }

  public void init() {

    asideUDPServerInit();
    appstierUDPServerInit();

  }

  public void runServer() {

    appstierUDPServerInit();
    asideUDPServerInit();

    asListenThread = new Thread(new AsPacketListener(this.asSocket, this.clientSideSocket,
        this.configManager.getAsBufferSize()));
    appstierListenThread = new Thread(new AppstierPacketListener(this.clientSideSocket,
        this.configManager.getAppstierListenBufferSize()));

    asListenThread.start();
    appstierListenThread.start();

  }



  public void appstierUDPServerInit() {

    int port = this.configManager.getAppstierListenPort();
    String[] ips = this.configManager.getAppestierListenIP().split(";");

    /*
     * May have multiple IP address to listen
     */
    for (String ip : ips) {
      try {

        log.info("Appstier listen ip: " + ip + " listen port: " + port);
        /**
         * Maybe just involve the port
         */
        clientSideSocket = new DatagramSocket(port);
        // clientSideSocket = new DatagramSocket(port, InetAddress.getByName(ip));

      } catch (SocketException e) {
        log.error("Cannot initiate UDP listenning. " + e.getMessage());
      }
    }

  }

  public void asideUDPServerInit() {

    int port = this.configManager.getAsListenPort();
    String ip = this.configManager.getAsListenAddress();

    /*
     * May have multiple IP address to listen
     */
    try {

      log.info("AS listen ip: " + ip + " listen port: " + port);
      /**
       * Maybe just involve the port
       */
      asSocket = new DatagramSocket(port);
      // asSocket = new DatagramSocket(port, InetAddress.getByName(ip));

    } catch (SocketException e) {
      log.error("Cannot initiate UDP listenning. ");
    }

  }


}
