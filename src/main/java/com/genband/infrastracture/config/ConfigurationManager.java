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
  private String appstierAddresses;
  private String appestierListenIP;
  private String asListenAddress;
  private String hazelCastIp;

  private int hazelCastPort;
  private int appstierPort;
  private int appstierListenPort;
  private int asListenPort;
  private int appstierListenBufferSize;
  private int asBufferSize;
  private int asServerPort;
  private int keepAlivedPort;

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
    this.appstierListenPort = Integer.parseInt(this.getPropertyValueByName("APPSTIER_LISTEN_PORT"));
    this.appestierListenIP = this.getPropertyValueByName("APPSTIER_LISTEN_IP_ADDRESSES");
    this.asListenAddress = this.getPropertyValueByName("AS_LISTEN_ADDRESS");
    this.asListenPort = Integer.parseInt(this.getPropertyValueByName("AS_LISTEN_PORT"));
    this.asBufferSize = Integer.parseInt(this.getPropertyValueByName("AS_BUFFER_SIZE"));
    this.appstierListenBufferSize =
        Integer.parseInt(this.getPropertyValueByName("APPSTIER_LISTEN_BUFFER_SIZE"));
    this.appstierAddresses = this.getPropertyValueByName("APPSTIER_IP_ADDRESSES");
    this.appstierPort = Integer.parseInt(this.getPropertyValueByName("APPSTIER_PORT"));
    this.hazelCastIp = this.getPropertyValueByName("HAZELCAST_DISCOVERY_IP");
    this.hazelCastPort = Integer.parseInt(this.getPropertyValueByName("HAZELCAST_PORT"));
    this.keepAlivedPort = Integer.parseInt(this.getPropertyValueByName("KEEPALIVED_PORT"));

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

  public int getAsListenPort() {
    return asListenPort;
  }

  public String getAsListenAddress() {
    return asListenAddress;
  }

  public int getAsBufferSize() {
    return asBufferSize;
  }

  public String getAppstierAddresses() {
    return appstierAddresses;
  }

  public int getAppstierPort() {
    return appstierPort;
  }

  public String getHazelCastIp() {
    return hazelCastIp;
  }

  public int getHazelCastPort() {
    return hazelCastPort;
  }

  public int getKeepAlivedPort() {
    return keepAlivedPort;
  }

  public String getAppestierListenIP() {
    return appestierListenIP;
  }

  public int getAppstierListenPort() {
    return appstierListenPort;
  }

  public int getAppstierListenBufferSize() {
    return appstierListenBufferSize;
  }

}
