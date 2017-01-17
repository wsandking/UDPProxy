package com.genband.infrastracture.hazelcast;

import java.util.Map;

import org.apache.log4j.Logger;

import com.genband.infrastracture.config.ConfigurationManager;
import com.genband.infrastracture.management.Address;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class UDPProxyHazelCastServer {

  private static final String addressMapName = "userIpportMapping";
  private static UDPProxyHazelCastServer instance;
  private HazelcastInstance hazelcastInstance;

  private static Logger log = Logger.getLogger(UDPProxyHazelCastServer.class.getName());

  private UDPProxyHazelCastServer() {

    Config cfg = new Config();

    /**
     * Enable hazelcast discovery
     */
    NetworkConfig network = cfg.getNetworkConfig();
    network.setPort(ConfigurationManager.getInstance().getHazelCastPort());
    network.setPortAutoIncrement(true);

    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);

    join.getTcpIpConfig().addMember(ConfigurationManager.getInstance().getHazelCastIp())
        .setEnabled(true);
    // network.getInterfaces().setEnabled(true).addInterface("172.28.250.*");

    this.hazelcastInstance = Hazelcast.newHazelcastInstance(cfg);
    log.info("Initializing Hazelcast... ");

  }

  public static UDPProxyHazelCastServer getInstance() {

    if (null == instance)
      instance = new UDPProxyHazelCastServer();
    return instance;

  }

  /**
   * All these for customize storage
   * 
   * @param hashMap
   * @return
   */
  public <K, V> Map<K, V> getHashMap(String hashMap) {

    Map<K, V> value = hazelcastInstance.getMap(hashMap);
    return value;

  }


  public <K, V> V getValueFromMapByKey(String hashMap, K key) {

    Map<K, V> value = hazelcastInstance.getMap(hashMap);
    V result = null;
    if (null != value)
      result = value.get(key);

    return result;
  }

  public <K, V> void addValueToMapByKey(String hashMap, K key, V val) {

    Map<K, V> value = hazelcastInstance.getMap(hashMap);
    value.put(key, val);

  }

  public void initAddressMap() {
    hazelcastInstance.getMap(addressMapName);
  }

  public Address getValueFromAddressMapByUsername(String username) {

    Map<String, Address> value = hazelcastInstance.getMap(addressMapName);
    Address address = value.get(username);
    return address;

  }

  // public Address getUsernameFromAddressMapByIp(String ip) {
  //
  // Map<String, Address> value = hazelcastInstance.getMap(addressMapName);
  // Address address = value.get(ip);
  // return address;
  //
  // }

  public void addUsernameAddressMap(String username, Address adress) {

    Map<String, Address> value = hazelcastInstance.getMap(addressMapName);
    value.put(username, adress);

    /**
     * Probably donot need another side operation, just making packets
     */
    // value.put(adress, username);
  }

}
