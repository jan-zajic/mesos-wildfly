
package org.apache.mesos.wildfly.scheduler.main.debug;

import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.mesos.wildfly.test.FrameworkStateSerialization;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jzajic
 */
public class LunchTaskRunnable implements Runnable
{

    private FrameworkStateSerialization serialization;
    private String endpoint;   

    public LunchTaskRunnable(FrameworkStateSerialization serialization, String endpoint)
    {
        this.serialization = serialization;
        this.endpoint = endpoint;
    }
    
    @Override
    public void run()
    {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            Entity<String> someEntity = Entity.entity(mapper.writeValueAsString(serialization), MediaType.APPLICATION_JSON);

            Client client = ClientBuilder.newBuilder().newClient();
            WebTarget target = client.target(endpoint);

            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Invocation invocation = invocationBuilder.buildPost(someEntity);
            invocation.invoke();

            client.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}
