
package org.apache.mesos.wildfly.executor.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.apache.mesos.wildfly.executor.WildFlyExecutor;
import org.apache.mesos.wildfly.executor.debug.TestExecutorDriver;
import org.apache.mesos.wildfly.rest.RestService;

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
    
}
