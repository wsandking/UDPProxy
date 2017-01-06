package com.genband.infrastracture.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * 
 * Load Property and Initialize System Configuration Map or Put the value out
 * 
 * @author sewang
 *
 */
public class ConfigurationManager {

  private final static String PROPERTIES_PATH = "/etc/udpproxy/udp_proxy.properties";
  private final static String DEFAULT_CONFIG_LOCATION = "udp_proxy.properties";

  private static Logger log = Logger.getLogger(ConfigurationManager.class.getName());

  private static ConfigurationManager instance;
  private Properties prop;

  private String asServerAddress;
  private String assignableIpAddresses;
  private String portRange;
  private int clientSidePort;
  private String clientSideIP;
  private int asListenPort;
  private String asListenAddress;
  private int clientMTU;
  private int asMTU;

  private int asServerPort;

  private ConfigurationManager() {
    File file = new File(PROPERTIES_PATH);
    prop = new Properties();

    if (file.exists()) {

      try (InputStream inputStream = new FileInputStream(PROPERTIES_PATH)) {
        prop.load(inputStream);
      } catch (IOException ex) {

        log.info("Cannot load system configuration");
        ex.printStackTrace();
      }

    } else {

      log.info("Cannot find config file, using default config instead");
      try (InputStream inputStream =
          ClassLoader.getSystemResourceAsStream(DEFAULT_CONFIG_LOCATION)) {
        prop.load(inputStream);
      } catch (IOException ex) {
        log.info("Cannot load class-level configuration file");
        ex.printStackTrace();
      }

    }
    initValue();
  }

  /**
   * Initialize systems
   */
  private void initValue() {
    // TODO Auto-generated method stub
    this.asServerAddress = this.getPropertyValueByName("AS_SERVER_ADDRESS");
    this.asServerPort = Integer.parseInt(this.getPropertyValueByName("AS_SERVER_PORT"));
    this.assignableIpAddresses = this.getPropertyValueByName("ASSIGNABLE_IP_ADDRESSES");
    this.portRange = this.getPropertyValueByName("PORT_RANGE");
    this.clientSidePort = Integer.parseInt(this.getPropertyValueByName("CLIENT_PORT"));
    this.clientSideIP = this.getPropertyValueByName("CLIENT_SIDE_IP_ADDRESSES");
    this.asListenAddress = this.getPropertyValueByName("AS_LISTEN_ADDRESS");
    this.asListenPort = Integer.parseInt(this.getPropertyValueByName("AS_LISTEN_PORT"));
    this.asMTU = Integer.parseInt(this.getPropertyValueByName("AS_MTU"));
    this.clientMTU = Integer.parseInt(this.getPropertyValueByName("CLIENT_MTU"));

  }

  public String getPropertyValueByName(String key) {
    return prop.getProperty(key);
  }

  public static ConfigurationManager getInstance() {
    if (null == ConfigurationManager.instance)
      instance = new ConfigurationManager();
    return ConfigurationManager.instance;
  }

  /*
   * Important Value
   */
  public String getAsServerAddress() {
    return asServerAddress;
  }

  public int getAsServerPort() {
    return asServerPort;
  }

  public String getAssignableIpAddresses() {
    return assignableIpAddresses;
  }

  public String getPortRange() {
    return portRange;
  }

  public int getClientSidePort() {
    return clientSidePort;
  }

  public String getClientSideIP() {
    return clientSideIP;
  }

  public int getAsListenPort() {
    return asListenPort;
  }

  public String getAsListenAddress() {
    return asListenAddress;
  }

  public int getClientMTU() {
    return clientMTU;
  }

  public int getAsMTU() {
    return asMTU;
  }


}
