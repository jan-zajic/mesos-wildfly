
package org.apache.mesos.wildfly.executor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Protos.CommandInfo;
import org.apache.mesos.Protos.ExecutorInfo;
import org.apache.mesos.Protos.TaskState;
import org.apache.mesos.Protos.TaskStatus;
import org.apache.mesos.wildfly.common.MesosWidlflyConfigProvider;
import org.apache.mesos.wildfly.common.MesosWildFlyConstants;
import org.apache.mesos.wildfly.common.UrlMesosWidlflyConfigProvider;
import org.apache.mesos.wildfly.json.JsonMapper;
import org.apache.mesos.wildfly.message.JsonMessage;
import org.apache.mesos.wildfly.message.ExecutorMessageHandler;
import org.apache.mesos.wildfly.message.MessageTypeQualifierLiteral;
import org.apache.mesos.wildfly.util.StreamRedirect;
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
    
    private ExecutorInfo executorInfo;    
    private String cmd;
    private volatile Protos.TaskInfo taskInfo;    
    private volatile Process process;
    private String profile;
    private String standaloneXmlUrl;
    
    @Inject @Any
    private Instance<ExecutorMessageHandler<?>> messageServices;
    @Inject
    private JsonMapper jsonMapper;
        
    @Override
    public void registered(ExecutorDriver driver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo)
    {
        URI configURI = findUrl(executorInfo, MesosWildFlyConstants.CONFIG_RESOURCE_PATH);        
        this.urlConfProvider = createUrlConfProvider(configURI);
        this.standaloneXmlUrl = findUri(executorInfo, "standalone.xml");
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
    public void launchTask(ExecutorDriver driver, Protos.TaskInfo taskInfo)
    {
        if(taskInfo != null)
        {
            executorInfo = taskInfo.getExecutor();
            startJboss(driver, taskInfo.getData().toStringUtf8());
        } else {            
            log.error("taskInfo is NULL!");
        }
    }
    
      /**
    * Starts a task's process so it goes into running state.
    */
    protected void startJboss(ExecutorDriver driver, String cmd) {
     reloadConfig();
     
     File sandboxHbaseBinary = new File(System.getProperty("user.dir"));
     Path sandboxHbaseBinaryPath = Paths.get(sandboxHbaseBinary.getAbsolutePath());
     
     log.info("The startProcess path is: " + sandboxHbaseBinaryPath);
     
     if (process == null) {
       try {
         ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", cmd);
         process = processBuilder.start();
         redirectProcess(process);
         // send task success
         driver.sendStatusUpdate(TaskStatus.newBuilder()
             .setTaskId(taskInfo.getTaskId())
             .setState(TaskState.TASK_RUNNING)
             .setData(taskInfo.getData()).build());
       } catch (IOException e) {
         log.error("Unable to start process:", e);
         process.destroy();
         // send task failed
         sendTaskFailed(driver);
       }
     } else {
       log.error("Tried to start process, but process already running");
     }
    }
    
    /**
      * Let the scheduler know that the task has failed.
      */
    private void sendTaskFailed(ExecutorDriver driver) 
    {
      driver.sendStatusUpdate(TaskStatus.newBuilder()
          .setTaskId(taskInfo.getTaskId())
          .setState(TaskState.TASK_FAILED)
          .build());
    }
    
    /**
      * Redirects a process to STDERR and STDOUT for logging and debugging purposes.
      */
    protected void redirectProcess(Process process) {
        StreamRedirect stdoutRedirect = new StreamRedirect(process.getInputStream(), System.out);
        stdoutRedirect.start();
        StreamRedirect stderrRedirect = new StreamRedirect(process.getErrorStream(), System.err);
        stderrRedirect.start();
    }
    
    @Override
    public void killTask(ExecutorDriver driver, Protos.TaskID taskId)
    {
        log.info("Killing task : " + taskId.getValue());
        if (process != null && taskId.equals(taskInfo.getTaskId())) {
          process.destroy();
          process = null;
        }
        driver.sendStatusUpdate(TaskStatus.newBuilder()
            .setTaskId(taskId)
            .setState(TaskState.TASK_KILLED)
            .build());
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
        // TODO let's shut down the driver more gracefully
        log.info("Executor asked to shutdown");
        if (taskInfo != null) {
          killTask(driver, taskInfo.getTaskId());
        }
    }

    @Override
    public void error(ExecutorDriver driver, String message)
    {
        log.error(this.getClass().getName() + ".error: " + message);
    }
    
    private URI findUrl(Protos.ExecutorInfo executorInfo, String part)
    {
        String uri = findUri(executorInfo, part);
        if(uri != null)
            return URI.create(uri);
        else
            return null;
    }
    
    private String findUri(Protos.ExecutorInfo executorInfo, String part)
    {
        for (CommandInfo.URI uri : executorInfo.getCommand().getUrisList()) 
        {
            if (uri.getValue().contains(part)) {
              return uri.getValue();
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

    private void reloadConfig()
    {
        getStandaloneXmlConfiguration(null, profile);
    }
    
}
