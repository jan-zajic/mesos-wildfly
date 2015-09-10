
package org.apache.mesos.wildfly.message;

import org.apache.mesos.ExecutorDriver;

/**
 *
 * @author jzajic
 */
public interface ExecutorMessageHandler<T extends JsonMessage>
{

    public void message(T message, ExecutorDriver driver);
        
    
}
