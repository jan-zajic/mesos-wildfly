
package org.apache.mesos.wildfly.executor.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.mesos.Protos;
import org.apache.mesos.wildfly.executor.WildFlyExecutor;
import org.apache.mesos.wildfly.executor.debug.TestExecutorDriver;
import org.apache.mesos.wildfly.rest.RestService;
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
    WildFlyExecutor wildFlyExecutor;
    
    @PUT
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putMessage(TestFrameworkMessage message) 
    {
        System.out.println("RECEIVED FRAMEWORK MESSAGE FROM SCHEDULER "+message);
        wildFlyExecutor.frameworkMessage(TestExecutorDriver.getInstance(), message.getData());
    }
    
    @POST
    @Path("/launchTask")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(FrameworkStateSerialization state) 
    {
        wildFlyExecutor.launchTask(TestExecutorDriver.getInstance(), state.getTaskInfo());
    }
    
}
