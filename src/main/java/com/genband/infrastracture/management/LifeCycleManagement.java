package com.genband.infrastracture.management;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.handlers.ManagementHandler;
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

  private DatagramSocket appstierSocket;
  private DatagramSocket asSocket;

  private ServerSocket managementSocket;

  private AsPacketListener asListener;
  private AppstierPacketListener appstierListener;



  public static LifeCycleManagement getInstance() {

    if (null == instance) {
      instance = new LifeCycleManagement();
    }

    return instance;

  }

  private LifeCycleManagement() {

    configManager = ConfigurationManager.getInstance();
    /**
     * Something to initialize here
     */

  }

  public void startListenning() {

    /**
     * Start management tcp port to listenning
     */

    try {

      managementSocket = new ServerSocket(configManager.getKeepAlivedPort());

      while (true) {

        Socket connectionSocket = managementSocket.accept();
        new Thread(new ManagementHandler(connectionSocket)).start();

      }

    } catch (IOException e) {

      log.error("Couldn't initialize listening port");
      e.printStackTrace();

    }

  }


  public void stopServer() {

    log.info("Shutting down server...");

    this.stopListener();

    if (asSocket != null && !asSocket.isClosed())
      this.asSocket.close();

    if (appstierSocket != null && !appstierSocket.isClosed())
      this.appstierSocket.close();

    asListenThread.interrupt();
    appstierListenThread.interrupt();

  }

  public void heartBeat(PrintWriter writer) throws IOException {

    writer.println("Server is ok... ");
    writer.flush();

  }

  public void init() {

    asideUDPServerInit();
    appstierUDPServerInit();

  }

  public void runServer() {

    init();

    asListener = new AsPacketListener(this.asSocket, this.appstierSocket,
        this.configManager.getAsBufferSize());
    appstierListener = new AppstierPacketListener(this.appstierSocket,
        this.configManager.getAppstierListenBufferSize());

    asListenThread = new Thread(asListener);
    appstierListenThread = new Thread(appstierListener);

    asListenThread.start();
    appstierListenThread.start();

  }

  public void stopListener() {


    asListener.closeListener();
    appstierListener.closeListener();

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
        appstierSocket = new DatagramSocket(port);
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
