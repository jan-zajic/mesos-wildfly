
package org.apache.mesos.wildfly.executor.debug;

import java.io.IOException;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;
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
        return Protos.Status.DRIVER_RUNNING;
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
    
    private Protos.ExecutorID generateExecutorId()
    {
        UUID randomUUID = UUID.randomUUID();        
        return Protos.ExecutorID.newBuilder().setValue(randomUUID.toString()).build();
    }
    
}
