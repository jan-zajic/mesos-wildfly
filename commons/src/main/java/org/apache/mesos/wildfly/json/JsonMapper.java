
package org.apache.mesos.wildfly.json;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import org.apache.mesos.wildfly.message.JsonMessage;
import org.apache.mesos.wildfly.message.protocol.Uptime;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class JsonMapper {

    private ObjectMapper mapper = new ObjectMapper();
    
    public JsonMapper()
    {
        mapper.getSerializationConfig().set(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }
     
    public JsonMessage readMessage(byte[] data)
    {
        try {
            return mapper.readValue(data, JsonMessage.class);
        } catch(IOException e) {
            throw new IllegalStateException("", e);
        }  
    }

    public byte[] messageData(JsonMessage message)
    {
        try {
            return mapper.writeValueAsBytes(message);
        } catch(IOException e) {
            throw new IllegalStateException("", e);
        }  
    }
    
}
