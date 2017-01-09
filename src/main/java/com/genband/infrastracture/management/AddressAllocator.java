package com.genband.infrastracture.management;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.exception.AddressException;


/**
 * Received API call and will assign a new IP address
 * 
 * @author sewang
 *
 */
public class AddressAllocator {


  private static Logger log = Logger.getLogger(AddressAllocator.class.getName());
  private static AddressAllocator instance;
  private List<String> avaiableIps;

  private Integer startPort;
  private Integer maxPort;
  private Integer currentPortIndex;
  private Integer ipIndex;



  public static AddressAllocator getInstance() {

    if (null == instance)
      instance = new AddressAllocator();
    return instance;

  }

  private AddressAllocator() {

    /**
     * Initialize the map
     */
    this.avaiableIps = new ArrayList<String>();


    /**
     * Try to get IP ports
     */
    try {

      for (String ip : ConfigurationManager.getInstance().getAssignableIpAddresses().split(";")) {
        this.avaiableIps.add(ip);
      }
      String[] ports = ConfigurationManager.getInstance().getPortRange().split("-");
      this.startPort = Integer.parseInt(ports[0]);
      this.maxPort = Integer.parseInt(ports[1]);
      this.currentPortIndex = this.maxPort;

    } catch (IndexOutOfBoundsException ex) {
      log.error(
          String.format("Address allocator initialize failure, because of %s", ex.getMessage()));
    }

  }

  /**
   * 
   * @return result[0] = ip, result[1] = port
   */
  public Address getAvailableAddress() throws AddressException {

    Address ad = new Address();
    ad.setPort(this.getPort());
    ad.setIpAddress(this.avaiableIps.get(this.ipIndex));

    return ad;


  }

  private int getPort() throws AddressException {
    // TODO Auto-generated method stub

    synchronized (this.currentPortIndex) {

      if (this.currentPortIndex + 1 <= this.maxPort) {
        this.currentPortIndex++;
      } else if (this.ipIndex + 1 < this.avaiableIps.size()) {
        this.ipIndex++;
        this.currentPortIndex = this.startPort;
      } else {
        /**
         * Address is used
         */
        throw new AddressException("Cannot allocate a new adress, all address is full");
      }

      int resultPort = this.currentPortIndex;
      return resultPort;

    }

  }

}
