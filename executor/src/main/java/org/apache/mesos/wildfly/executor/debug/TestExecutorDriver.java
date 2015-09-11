
package org.apache.mesos.wildfly.executor.debug;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.wildfly.test.FrameworkStateSerialization;
import org.apache.mesos.wildfly.test.TestFrameworkMessage;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jzajic
 */
public class TestExecutorDriver implements ExecutorDriver
{

    private static TestExecutorDriver instance;
    
    private Protos.ExecutorID id;
    private Executor executor;
    private volatile boolean running = true;
    
    private TestExecutorDriver(Executor executor)
    {
        super();
        this.id = generateExecutorId();
        this.executor = executor;        
    }
    
    public static synchronized TestExecutorDriver getInstance(Executor scheduler)
    {
        instance = new TestExecutorDriver(scheduler);
        return instance;
    }

    public static TestExecutorDriver getInstance()
    {
        if(instance == null)
            throw new IllegalStateException("not initialized yet");
        return instance;
    }
    
    @Override
    public Protos.Status start()
    {
        FrameworkStateSerialization frameworkMessage = restGet("http://localhost:8080/test/register/"+id.getValue(), FrameworkStateSerialization.class);
        executor.registered(this, frameworkMessage.getExecutorInfo(), frameworkMessage.getFrameworkInfo(), frameworkMessage.getSlaveInfo());
        return Protos.Status.DRIVER_RUNNING;
    }

    @Override
    public Protos.Status stop()
    {
        this.running = false;
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
        while(running)
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                break;
            }
        }
        return Protos.Status.DRIVER_STOPPED;
    }

    @Override
    public Protos.Status run()
    {
        Protos.Status status = start();
        return status != Protos.Status.DRIVER_RUNNING ? status : join();
    }

    @Override
    public Protos.Status sendStatusUpdate(Protos.TaskStatus status)
    {
        return Protos.Status.DRIVER_RUNNING;
    }
    
    @Override
    public Protos.Status sendFrameworkMessage(byte[] data)    
    {
        TestFrameworkMessage message = new TestFrameworkMessage();
        message.setData(data);
        message.setExecutorId(id.getValue());
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            Entity<String> someEntity = Entity.entity(mapper.writeValueAsString(message), MediaType.APPLICATION_JSON);

            Client client = ClientBuilder.newBuilder().newClient();
            WebTarget target = client.target("http://localhost:8080/test/message");

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Invocation invocation = invocationBuilder.buildPut(someEntity);
            invocation.invoke();

            client.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return Protos.Status.DRIVER_RUNNING;
    }
    
    private <T> T restGet(String url, Class<T> resultClass)
    {
        ObjectMapper mapper = new ObjectMapper();
        
        try {          
            Client client = ClientBuilder.newBuilder().newClient();
            WebTarget target = client.target(url);

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Invocation invocation = invocationBuilder.buildGet();
            Response response = invocation.invoke();
            try {
                if(response.getStatus()==200)
                    return mapper.readValue(response.readEntity(String.class), resultClass);
                else
                    throw new  IllegalStateException("illegal status code "+response.getStatus());
            } finally {
                client.close();
            }
        } catch(IOException e) {
            throw new IllegalStateException("",e);
        }        
    }
    
    private Protos.ExecutorID generateExecutorId()
    {
        UUID randomUUID = UUID.randomUUID();        
        return Protos.ExecutorID.newBuilder().setValue(randomUUID.toString()).build();
    }
    
}
