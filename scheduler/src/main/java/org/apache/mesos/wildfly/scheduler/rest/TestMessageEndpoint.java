
package org.apache.mesos.wildfly.scheduler.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.mesos.Protos;
import org.apache.mesos.wildfly.json.JsonMapper;
import org.apache.mesos.wildfly.message.protocol.UptimeRequest;
import org.apache.mesos.wildfly.rest.RestService;
import org.apache.mesos.wildfly.scheduler.WildFlyScheduler;
import org.apache.mesos.wildfly.scheduler.main.debug.LunchTaskRunnable;
import org.apache.mesos.wildfly.scheduler.main.debug.TestSchedulerDriver;
import org.apache.mesos.wildfly.test.FrameworkStateSerialization;
import org.apache.mesos.wildfly.test.TestFrameworkMessage;

/**
 *
 * @author jzajic
 */
@Path("/test")
@ApplicationScoped
public class TestMessageEndpoint implements RestService {

    @Inject
    WildFlyScheduler wildFlyScheduler;
    @Inject
    JsonMapper mapper;
    
    @PUT
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putMessage(TestFrameworkMessage message) 
    {
        String executorIdValue = message.getExecutorId();
        String slaveIdValue = message.getSlaveId();
        Protos.ExecutorID executorID = executorIdValue != null ? Protos.ExecutorID.newBuilder().setValue(executorIdValue).build() : null;
        Protos.SlaveID slaveID = slaveIdValue != null ? Protos.SlaveID.newBuilder().setValue(slaveIdValue).build() : null;
        wildFlyScheduler.frameworkMessage(TestSchedulerDriver.getInstance(), executorID, slaveID, message.getData());
    }
    
    @GET
    @Path("/register/{executorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public FrameworkStateSerialization register(@PathParam("executorId") String executorId) 
    {
        FrameworkStateSerialization serialization = new FrameworkStateSerialization();
        Protos.ExecutorInfo executorInfo = Protos.ExecutorInfo.newBuilder()                    
                .setExecutorId(Protos.ExecutorID.newBuilder().setValue(executorId).build())
                .setCommand(Protos.CommandInfo.newBuilder().build())
                .build();
        serialization.setExecutorInfo(executorInfo);
        Thread thread = new Thread(new LunchTaskRunnable(serialization, "http://localhost:8081/test/launchTask"));
        thread.start();
        return serialization;
    }
    
    @GET
    @Path("/ping")
    public String ping()
    {
        TestSchedulerDriver.getInstance().sendFrameworkMessage(null, null, mapper.messageData(new UptimeRequest()));
        return "pinged";
    }
    
}
