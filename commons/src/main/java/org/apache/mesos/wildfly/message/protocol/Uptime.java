
package org.apache.mesos.wildfly.message.protocol;

import org.apache.mesos.wildfly.message.JsonMessage;

/**
 *
 * @author jzajic
 */
public class Uptime implements JsonMessage 
{
    
    private long time;
    
    public long getTime()
    {
        return time;
    }
    
    public void setTime(long time)
    {
        this.time = time;
    }
    
}
