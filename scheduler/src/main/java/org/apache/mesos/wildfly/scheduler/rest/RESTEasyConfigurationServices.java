
package org.apache.mesos.wildfly.scheduler.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.mesos.wildfly.common.MesosWidlflyFrameworkConfiguration;
import org.apache.mesos.wildfly.common.MesosWidlflyFrameworkConfigurationProvider;
import org.apache.mesos.wildfly.scheduler.main.RestService;

/**
 *
 * @author jzajic
 */
@Path("/config")
@ApplicationScoped
public class RESTEasyConfigurationServices implements RestService
{

    @Inject
    private MesosWidlflyFrameworkConfigurationProvider configProvider;
    
    @GET
    @Produces("application/json")
    public MesosWidlflyFrameworkConfiguration produceJSON() 
    {
        return configProvider.getConfiguration();
    }
    
}
