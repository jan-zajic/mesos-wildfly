
package org.apache.mesos.wildfly.scheduler.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.mesos.wildfly.common.MesosWidlflyConfig;
import org.apache.mesos.wildfly.common.MesosWidlflyConfigProvider;
import org.apache.mesos.wildfly.common.MesosWildFlyConstants;
import org.apache.mesos.wildfly.rest.RestService;

/**
 *
 * @author jzajic
 */
@Path(MesosWildFlyConstants.CONFIG_RESOURCE_PATH)
@ApplicationScoped
public class RESTEasyConfigurationServices implements RestService
{
    
    @Inject
    private MesosWidlflyConfigProvider configProvider;
    
    @GET
    @Produces("application/json")
    public MesosWidlflyConfig produceJSON() 
    {
        return configProvider.getConfiguration();
    }
    
}
