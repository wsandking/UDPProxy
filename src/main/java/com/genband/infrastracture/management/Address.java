package com.genband.infrastracture.management;

import java.io.Serializable;

public class Address implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = -712726298012254453L;
  
  private String ipAddress;
  private int port;

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
  
  
}
