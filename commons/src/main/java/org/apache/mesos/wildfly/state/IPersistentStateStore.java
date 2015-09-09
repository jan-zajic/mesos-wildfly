package org.apache.mesos.wildfly.state;

import org.apache.mesos.Protos;

/**
 * Entry point for persistence for the WildFly scheduler.
 */
public interface IPersistentStateStore 
{
    
  /* RAW METHODS */  
  byte[] getRawValueForId(String id);
  void setRawValueForId(String id, byte[] frameworkId);
  <T extends Object> T get(String key);
  <T extends Object> void set(String key, T object);
  
}
