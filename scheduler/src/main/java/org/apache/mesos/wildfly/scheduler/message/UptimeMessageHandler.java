
package org.apache.mesos.wildfly.scheduler.message;

import javax.enterprise.context.ApplicationScoped;
import org.apache.mesos.Protos;
import org.apache.mesos.wildfly.message.MessageTypeQualifier;
import org.apache.mesos.wildfly.message.SchedulerMessageHandler;
import org.apache.mesos.wildfly.message.protocol.Uptime;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
@MessageTypeQualifier(Uptime.class)
public class UptimeMessageHandler implements SchedulerMessageHandler<Uptime>
{
    
    @Override
    public void message(Uptime message, Protos.ExecutorID executorId, Protos.SlaveID slaveId)
    {
        System.out.println("Uptime of "+executorId+": "+message.getTime());
    }
    
}
