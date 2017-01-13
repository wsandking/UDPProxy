package com.genband.infrastracture.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.hazelcast.UDPProxyHazelCastServer;
import com.genband.infrastracture.listener.AsPacketListener;
import com.genband.infrastracture.listener.ClientPacketListener;

/**
 * 
 * @author sewang
 *
 */
public class PortMappingUDPProxy {


  private DatagramSocket clientSideSocket;
  private DatagramSocket asSocket;

  private Integer debugListener;

  private ConfigurationManager configManager;

  private static Logger log = Logger.getLogger(PortMappingUDPProxy.class.getName());

  public PortMappingUDPProxy() {

    /**
     * Initialize Hazelcast Server and initialMap
     */
    UDPProxyHazelCastServer.getInstance().initAddressMap();
    this.configManager = ConfigurationManager.getInstance();
    clientSideUDPServerInit();
    asideUDPServerInit();

  }

  public void clientSideUDPServerInit() {

    int port = this.configManager.getClientSidePort();
    String[] ips = this.configManager.getClientSideIP().split(";");

    /*
     * May have multiple IP address to listen
     */
    for (String ip : ips) {
      try {

        log.info("Appstier listen ip: " + ip + " listen port: " + port);
        clientSideSocket = new DatagramSocket(port, InetAddress.getByName(ip));

      } catch (SocketException e) {
        log.error("Cannot initiate UDP listenning. " + e.getMessage());
      } catch (UnknownHostException e) {
        log.error("Cannot listen on ip: " + ip + " reason: " + e.getMessage());
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
      asSocket = new DatagramSocket(port, InetAddress.getByName(ip));

    } catch (SocketException e) {
      log.error("Cannot initiate UDP listenning. ");
    } catch (UnknownHostException e) {
      log.error("Cannot listen on ip: " + ip);
    }

  }

  /**
   * Start two servers, listen on two different port
   */
  public void runServer() {

    /**
     * Run two threads that listen on two different sockets
     */
    new Thread(new AsPacketListener(this.asSocket, this.clientSideSocket,
        this.configManager.getAsBufferSize())).start();
    new Thread(
        new ClientPacketListener(this.clientSideSocket, this.configManager.getClientBufferSize()))
            .start();

    if (null != debugListener) {
      log.info("Debug mode. Initialize debug Listener. ");
    }

  }

  public static void main(String args[]) {

    BasicConfigurator.configure();
    PortMappingUDPProxy pmUDPProxy = new PortMappingUDPProxy();
    if (args.length > 1)
      pmUDPProxy.debugListener = 10;
    pmUDPProxy.runServer();

  }

}
