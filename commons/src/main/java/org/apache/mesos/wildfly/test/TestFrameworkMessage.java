
package org.apache.mesos.wildfly.test;

/**
 *
 * @author jzajic
 */
public class TestFrameworkMessage 
{

    private byte[] data;
    String executorId;
    String slaveId;

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    public String getExecutorId()
    {
        return executorId;
    }

    public void setExecutorId(String executorId)
    {
        this.executorId = executorId;
    }

    public String getSlaveId()
    {
        return slaveId;
    }

    public void setSlaveId(String slaveId)
    {
        this.slaveId = slaveId;
    }
    
}
