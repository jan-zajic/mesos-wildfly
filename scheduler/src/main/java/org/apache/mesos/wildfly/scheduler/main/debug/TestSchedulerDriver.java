
package org.apache.mesos.wildfly.scheduler.main.debug;

import java.io.IOException;
import java.util.Collection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.apache.mesos.wildfly.test.TestFrameworkMessage;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jzajic
 */
public class TestSchedulerDriver implements SchedulerDriver
{
  
    private static TestSchedulerDriver instance;
    
    private Scheduler scheduler;
    
    private TestSchedulerDriver(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
    
    public static synchronized TestSchedulerDriver getInstance(Scheduler scheduler)
    {
        instance = new TestSchedulerDriver(scheduler);
        return instance;
    }

    public static TestSchedulerDriver getInstance()
    {
        if(instance == null)
            throw new IllegalStateException("not initialized yet");
        return instance;
    }
    
    
    
    @Override
    public Protos.Status start()
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status stop(boolean failover)
    {
        return Protos.Status.DRIVER_STOPPED;
    }

    @Override
    public Protos.Status stop()
    {
        return Protos.Status.DRIVER_STOPPED;
    }

    @Override
    public Protos.Status abort()
    {
        return Protos.Status.DRIVER_ABORTED;
    }

    @Override
    public Protos.Status join()
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status run()
    {
        
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status requestResources(Collection<Protos.Request> requests)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status launchTasks(Collection<Protos.OfferID> offerIds, Collection<Protos.TaskInfo> tasks, Protos.Filters filters)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status launchTasks(Collection<Protos.OfferID> offerIds, Collection<Protos.TaskInfo> tasks)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status launchTasks(Protos.OfferID offerId, Collection<Protos.TaskInfo> tasks, Protos.Filters filters)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status launchTasks(Protos.OfferID offerId, Collection<Protos.TaskInfo> tasks)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status killTask(Protos.TaskID taskId)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status acceptOffers(Collection<Protos.OfferID> offerIds, Collection<Protos.Offer.Operation> operations, Protos.Filters filters)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status declineOffer(Protos.OfferID offerId, Protos.Filters filters)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status declineOffer(Protos.OfferID offerId)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status reviveOffers()
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status acknowledgeStatusUpdate(Protos.TaskStatus status)
    {
        return Protos.Status.DRIVER_RUNNING;
    }
    
    @Override
    public Protos.Status sendFrameworkMessage(Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data)
    {
        TestFrameworkMessage message = new TestFrameworkMessage();
        message.setData(data);
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            Entity<String> someEntity = Entity.entity(mapper.writeValueAsString(message), MediaType.APPLICATION_JSON);

            Client client = ClientBuilder.newBuilder().newClient();
            WebTarget target = client.target("http://localhost:8081/test/message");

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Invocation invocation = invocationBuilder.buildPut(someEntity);
            invocation.invoke();

            client.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return Protos.Status.DRIVER_RUNNING;
    }
    
    @Override
    public Protos.Status reconcileTasks(Collection<Protos.TaskStatus> statuses)
    {
        return Protos.Status.DRIVER_RUNNING;
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }
    
}
