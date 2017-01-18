package com.genband.infrastracture.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.hazelcast.UDPProxyHazelCastServer;
import com.genband.infrastracture.management.LifeCycleManagement;

/**
 * 
 * Mostly, start an Hazelcast server and log initializer
 * 
 * 
 * @author sewang
 *
 */
public class PortMappingUDPProxy {

  private static Logger log = Logger.getLogger(PortMappingUDPProxy.class.getName());

  private Integer debugListener;

  public PortMappingUDPProxy() {

    /**
     * Initialize Hazelcast Server and initialMap
     */
    UDPProxyHazelCastServer.getInstance().initAddressMap();
    if (null != debugListener) {
      log.info("Debug mode. Initialize debug Listener. ");
    }

    LifeCycleManagement.getInstance().startListenning();

    /**
     * Before initialize listenning socket, it should wait for keepalived signal
     */
  }

  public static void main(String args[]) {

    BasicConfigurator.configure();
    PortMappingUDPProxy pmUDPProxy = new PortMappingUDPProxy();
    if (args.length > 1)
      pmUDPProxy.debugListener = 10;

  }

}
