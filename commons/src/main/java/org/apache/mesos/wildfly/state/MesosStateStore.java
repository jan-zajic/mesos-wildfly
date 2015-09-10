package org.apache.mesos.wildfly.state;

import org.apache.mesos.state.State;
import org.apache.mesos.state.Variable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Manages persistence state of the application using mesos State abstraction
 */
public class MesosStateStore implements IPersistentStateStore {

  private State state;
  
  public MesosStateStore(State state) 
  {
    this.state = state;
  }
  
  public byte[] getRawValueForId(String id) {
      try {
        byte[] value = state.fetch(id).get().value();
        return value;
      } catch(ExecutionException | InterruptedException e) {
        throw new IllegalStateException("cannot get raw value for id "+id, e);
      }
  }

  public void setRawValueForId(String id, byte[] newRawValue) {
      try {
        Variable value = state.fetch(id).get();
        if (newRawValue == null)
          value = value.mutate(new byte[]{});
        else
          value = value.mutate(newRawValue);
        state.store(value).get();
      } catch(ExecutionException | InterruptedException e) {
        throw new IllegalStateException("cannot set raw value for id "+id, e);
      }
  }

  /**
   * Get serializable object from store.
   *
   * @return serialized object or null if none
   * @throws ExecutionException
   * @throws InterruptedException
   * @throws java.io.IOException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public <T extends Object> T get(String key) {
    try {
        byte[] existingNodes = state.fetch(key).get().value();
        if (existingNodes.length > 0) {
          try (ByteArrayInputStream bis = new ByteArrayInputStream(existingNodes);
               ObjectInputStream in = new ObjectInputStream(bis);) 
          {                           
            // generic in java lose their runtime information, there is no way to get this casted
            // without
            // the need for the SuppressWarnings on the method.
            return (T) in.readObject();
          }
        } else {
          return null;
        }
    } catch(ExecutionException | InterruptedException | IOException | ClassNotFoundException e) {
        throw new IllegalStateException("cannot get value for key "+key, e);
    }
  }
  
  /**
   * Set serializable object in store.
   *
   * @throws ExecutionException
   * @throws InterruptedException
   * @throws IOException
   */
  public <T extends Object> void set(String key, T object) {
    try {
        Variable value = state.fetch(key).get();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos);) 
        {          
          out.writeObject(object);
          value = value.mutate(bos.toByteArray());
          state.store(value).get();
        }      
    } catch(ExecutionException | InterruptedException | IOException e) {
        throw new IllegalStateException("cannot set value for key "+key, e);
    }
  }
  
}
