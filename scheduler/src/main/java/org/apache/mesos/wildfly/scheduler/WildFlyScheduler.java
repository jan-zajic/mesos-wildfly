
package org.apache.mesos.wildfly.scheduler;

import java.io.IOException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.apache.mesos.wildfly.json.JsonMapper;
import org.apache.mesos.wildfly.message.JsonMessage;
import org.apache.mesos.wildfly.message.MessageTypeQualifierLiteral;
import org.apache.mesos.wildfly.message.SchedulerMessageHandler;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class WildFlyScheduler implements Scheduler
{
    
    @Inject
    private JsonMapper jsonMapper;
    
    @Inject @Any
    private Instance<SchedulerMessageHandler<?>> messageServices;
    
    public void registered(SchedulerDriver driver, Protos.FrameworkID frameworkId, Protos.MasterInfo masterInfo)
    {
        
    }

    public void reregistered(SchedulerDriver driver, Protos.MasterInfo masterInfo)
    {
    }

    public void resourceOffers(SchedulerDriver driver, List<Protos.Offer> offers)
    {
    }

    public void offerRescinded(SchedulerDriver driver, Protos.OfferID offerId)
    {
    }

    public void statusUpdate(SchedulerDriver driver, Protos.TaskStatus status)
    {
    }
    
    @Override
    public void frameworkMessage(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data)
    {        
        JsonMessage message = jsonMapper.readMessage(data);
        SchedulerMessageHandler messageService = messageServices.select(new MessageTypeQualifierLiteral(message.getClass())).get();
        messageService.message(message, executorId, slaveId);       
    }
    
    public void disconnected(SchedulerDriver driver)
    {
    }

    public void slaveLost(SchedulerDriver driver, Protos.SlaveID slaveId)
    {
    }

    public void executorLost(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, int status)
    {
    }

    public void error(SchedulerDriver driver, String message)
    {
    }

}
