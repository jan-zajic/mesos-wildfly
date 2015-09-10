
package org.apache.mesos.wildfly.message;

import org.apache.mesos.Protos;

/**
 *
 * @author jzajic
 */
public interface SchedulerMessageHandler<T extends JsonMessage>
{

    public void message(T message, Protos.ExecutorID executorId, Protos.SlaveID slaveId);        
    
}
