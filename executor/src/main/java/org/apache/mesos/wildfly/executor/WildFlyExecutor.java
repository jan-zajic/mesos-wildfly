
package org.apache.mesos.wildfly.executor;

import java.net.URI;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Protos.CommandInfo;
import org.apache.mesos.wildfly.common.MesosWidlflyConfigProvider;
import org.apache.mesos.wildfly.common.MesosWildFlyConstants;
import org.apache.mesos.wildfly.common.UrlMesosWidlflyConfigProvider;
import org.apache.mesos.wildfly.json.JsonMapper;
import org.apache.mesos.wildfly.message.JsonMessage;
import org.apache.mesos.wildfly.message.ExecutorMessageHandler;
import org.apache.mesos.wildfly.message.MessageTypeQualifierLiteral;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jzajic
 */
public class WildFlyExecutor implements Executor 
{
    
    private final Logger log = LoggerFactory.getLogger(WildFlyExecutor.class);
    
    private MesosWidlflyConfigProvider urlConfProvider;
    
    @Inject @Any
    private Instance<ExecutorMessageHandler<?>> messageServices;
    @Inject
    private JsonMapper jsonMapper;
        
    @Override
    public void registered(ExecutorDriver driver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo)
    {
        URI configURI = findUrl(executorInfo, MesosWildFlyConstants.CONFIG_RESOURCE_PATH);
        this.urlConfProvider = createUrlConfProvider(configURI);
        log.info("Executor registered with the slave");
    }
    
    @Override
    public void reregistered(ExecutorDriver driver, Protos.SlaveInfo slaveInfo)
    {
        log.info("Executor re-registered with the slave");
    }
    
    @Override
    public void disconnected(ExecutorDriver driver)
    {
        log.info("Executor driver disconnected");
    }
    
    @Override
    public void launchTask(ExecutorDriver driver, Protos.TaskInfo task)
    {
    }

    @Override
    public void killTask(ExecutorDriver driver, Protos.TaskID taskId)
    {
    }
    
    @Override
    public void frameworkMessage(ExecutorDriver driver, byte[] data)
    {        
        log.info("RECEIVED FRAMEWORK MESSAGE FROM SCHEDULER");
        JsonMessage message = jsonMapper.readMessage(data);
        ExecutorMessageHandler messageService = messageServices.select(new MessageTypeQualifierLiteral(message.getClass())).get();
        messageService.message(message, driver);      
    }

    @Override
    public void shutdown(ExecutorDriver driver)
    {
    }

    @Override
    public void error(ExecutorDriver driver, String message)
    {
    }
    
    private URI findUrl(Protos.ExecutorInfo executorInfo, String part)
    {
        for (CommandInfo.URI uri : executorInfo.getCommand().getUrisList()) 
        {
            if (uri.getValue().contains(part)) {
              return URI.create(uri.getValue());
            }
        }
        return null;
    }
    
    private MesosWidlflyConfigProvider createUrlConfProvider(URI url)
    {
        UrlMesosWidlflyConfigProvider provider = new UrlMesosWidlflyConfigProvider(url);
        return provider;
    }
    
    private String getStandaloneXmlConfiguration(String baseUrl, String profile)
    {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(baseUrl)
                .path(MesosWildFlyConstants.STANDALONE_XML_RESOURCE_PATH)
                .path(profile);
        Response response = target.request().get();
        try {               
            if (response.getStatus() != 200) 
            {
                throw new RuntimeException("Failed : HTTP error code : "
                                + response.getStatus());
            }
            return response.readEntity(String.class);
        } finally {
            response.close();  // You should close connections!
        }        
    }
    
}
