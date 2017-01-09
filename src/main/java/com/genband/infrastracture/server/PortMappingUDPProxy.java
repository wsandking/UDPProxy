package com.genband.infrastracture.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.hazelcast.UDPProxyHazelCastServer;

/**
 * 
 * @author sewang
 *
 */
public class PortMappingUDPProxy {


  private DatagramSocket clientSideSocket;
  private DatagramSocket asSocket;

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
        clientSideSocket = new DatagramSocket(port, InetAddress.getByName(ip));
      } catch (SocketException e) {
        log.error("Cannot initiate UDP listenning. ");
      } catch (UnknownHostException e) {
        log.error("Cannot listen on ip: " + ip);
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
      clientSideSocket = new DatagramSocket(port, InetAddress.getByName(ip));
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
    new Thread(new AsPacketListener(this.asSocket, this.configManager.getAsMTU())).start();
    new Thread(new ClientPacketListener(this.clientSideSocket, this.configManager.getClientMTU()))
        .start();

  }

  public static void main(String args[]) {

    BasicConfigurator.configure();
    PortMappingUDPProxy pmUDPProxy = new PortMappingUDPProxy();
    pmUDPProxy.runServer();

  }

}
