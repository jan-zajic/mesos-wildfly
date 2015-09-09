
package org.apache.mesos.wildfly.scheduler;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class WildFlyScheduler implements Scheduler
{
    
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

    public void frameworkMessage(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data)
    {
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
