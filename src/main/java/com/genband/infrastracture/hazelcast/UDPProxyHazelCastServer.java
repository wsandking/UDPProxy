package com.genband.infrastracture.hazelcast;

import java.util.Map;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class UDPProxyHazelCastServer {

  private static UDPProxyHazelCastServer instance;
  private HazelcastInstance hazelcastInstance;

  private static Logger log = Logger.getLogger(UDPProxyHazelCastServer.class.getName());

  private UDPProxyHazelCastServer() {

    Config cfg = new Config();
    this.hazelcastInstance = Hazelcast.newHazelcastInstance(cfg);
    log.info("Initializing Hazelcast... ");

  }

  public static UDPProxyHazelCastServer getInstance() {

    if (null == instance)
      instance = new UDPProxyHazelCastServer();
    return instance;

  }

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

}
