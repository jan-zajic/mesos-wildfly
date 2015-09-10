
package org.apache.mesos.wildfly.executor.message;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.wildfly.json.JsonMapper;
import org.apache.mesos.wildfly.message.ExecutorMessageHandler;
import org.apache.mesos.wildfly.message.MessageTypeQualifier;
import org.apache.mesos.wildfly.message.protocol.Uptime;
import org.apache.mesos.wildfly.message.protocol.UptimeRequest;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
@MessageTypeQualifier(UptimeRequest.class)
public class UptimeRequestMessageHandler implements ExecutorMessageHandler<UptimeRequest>
{
    
    @Inject
    private JsonMapper jsonMapper;
    
    @Override
    public void message(UptimeRequest message, ExecutorDriver driver)
    {
        Uptime response = new Uptime();
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getUptime();
        response.setTime(uptime);
        driver.sendFrameworkMessage(jsonMapper.messageData(response));
    }
    
}
