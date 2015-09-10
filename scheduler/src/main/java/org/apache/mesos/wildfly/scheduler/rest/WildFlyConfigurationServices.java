
package org.apache.mesos.wildfly.scheduler.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.mesos.wildfly.common.MesosWildFlyConstants;

/**
 *
 * @author jzajic
 */
@Path(MesosWildFlyConstants.WILDFLY_RESOURCE_PATH)
@ApplicationScoped
public class WildFlyConfigurationServices 
{
    
    @GET
    @Path(MesosWildFlyConstants.STANDALONE_XML_RESOURCE_PATH+"/{profile}")
    public Response getStandaloneXml(@PathParam(value = "profile") String profile) 
    {
        ResponseBuilder response = Response.ok("file");
        response.type("application/xml");
        response.header("Content-Disposition", "attachment; filename=\"file.doc\"");
        return response.build();
    }
    
    @GET
    @Path(MesosWildFlyConstants.HOST_XML_RESOURCE_PATH+"/{group}")
    public Response getHostXml(@PathParam(value = "group") String group) 
    {
        ResponseBuilder response = Response.ok("file");
        response.type("application/xml");
        response.header("Content-Disposition", "attachment; filename=\"file.doc\"");
        return response.build();
    }
    
}
