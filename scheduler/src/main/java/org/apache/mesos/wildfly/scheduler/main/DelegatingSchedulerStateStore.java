
package org.apache.mesos.wildfly.scheduler.main;

import org.apache.mesos.wildfly.scheduler.main.InitializationContext;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.mesos.Protos;
import org.apache.mesos.wildfly.common.MesosWildFlyConstants;
import org.apache.mesos.wildfly.persistence.SchedulerStateStore;
import org.apache.mesos.wildfly.state.IPersistentStateStore;

/**
 *
 * @author jzajic
 */
public class DelegatingSchedulerStateStore implements SchedulerStateStore
{

    private final InitializationContext initializationContext;

    public DelegatingSchedulerStateStore(InitializationContext initializationContext)
    {
        this.initializationContext = initializationContext;
    }
    
    @Override
     public void setFrameworkId(Protos.FrameworkID id) {       
         if(id != null)
           getStore().setRawValueForId(MesosWildFlyConstants.ZK_FRAMEWORK_ID_KEY, id.toByteArray());
         else
           getStore().setRawValueForId(MesosWildFlyConstants.ZK_FRAMEWORK_ID_KEY, null);       
     }
     
     @Override
     public Protos.FrameworkID getFrameworkId() {
       Protos.FrameworkID frameworkID = null;
       byte[] existingFrameworkId = getStore().getRawValueForId(MesosWildFlyConstants.ZK_FRAMEWORK_ID_KEY);
              
       if (existingFrameworkId.length > 0) {
          try {
            frameworkID = Protos.FrameworkID.parseFrom(existingFrameworkId);
          } catch(InvalidProtocolBufferException ex) {
              throw new IllegalStateException("error while reading frameworkID from persistente state",ex);
          }
       }       
        
       return frameworkID;
     }
    
    private IPersistentStateStore getStore()
    {
        IPersistentStateStore store = initializationContext.getRawIPersistentStateStore();
        if(store == null)
            throw new IllegalStateException("cannot access IPersistentStateStore - not initialized yet");
        else
            return store;
    }
     
}
