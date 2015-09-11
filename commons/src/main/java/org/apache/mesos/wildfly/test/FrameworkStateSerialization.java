
package org.apache.mesos.wildfly.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.mesos.Protos;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author jzajic
 */
public class FrameworkStateSerialization implements Serializable
{

    private byte[] executorInfoData;
    private byte[] frameworkInfoData;
    private byte[] taskInfoData;
    private byte[] slaveInfoData;

    public byte[] getExecutorInfoData()
    {
        return executorInfoData;
    }

    public void setExecutorInfoData(byte[] executorInfoData)
    {
        this.executorInfoData = executorInfoData;
    }

    public byte[] getFrameworkInfoData()
    {
        return frameworkInfoData;
    }

    public void setFrameworkInfoData(byte[] frameworkInfo)
    {
        this.frameworkInfoData = frameworkInfo;
    }

    public byte[] getTaskInfoData()
    {
        return taskInfoData;
    }

    public void setTaskInfoData(byte[] taskInfo)
    {
        this.taskInfoData = taskInfo;
    }

    public byte[] getSlaveInfoData()
    {
        return slaveInfoData;
    }

    public void setSlaveInfoData(byte[] slaveInfo)
    {
        this.slaveInfoData = slaveInfo;
    }

    @JsonIgnore
    public Protos.ExecutorInfo getExecutorInfo()
    {
        return deserialize(executorInfoData, Protos.ExecutorInfo.class);
    }
    
    @JsonIgnore
    public Protos.FrameworkInfo getFrameworkInfo()
    {
        return deserialize(frameworkInfoData, Protos.FrameworkInfo.class);
    }
    
    @JsonIgnore
    public Protos.SlaveInfo getSlaveInfo()
    {
        return deserialize(slaveInfoData, Protos.SlaveInfo.class);
    }
    
    @JsonIgnore
    public Protos.TaskInfo getTaskInfo()
    {
        return deserialize(taskInfoData, Protos.TaskInfo.class);
    }
    
    @JsonIgnore
    public void setFrameworkInfo(Protos.FrameworkInfo frameworkInfo)
    {
        this.frameworkInfoData = serialize(frameworkInfo);
    }

    @JsonIgnore
    public void setTaskInfo(Protos.TaskInfo taskInfo)
    {
        this.taskInfoData = serialize(taskInfo);
    }
  
    @JsonIgnore
    public void setExecutorInfo(Protos.ExecutorInfo executorInfo)
    {
        this.executorInfoData = serialize(executorInfo);
    }

    @JsonIgnore
    public void setSlaveInfo(Protos.SlaveInfo slaveInfo)
    {
        this.slaveInfoData = serialize(slaveInfo);
    }
    
    private <T> T deserialize(byte[] data, Class<T> type)
    {
        if(data == null)
            return null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream oi = new ObjectInputStream(bis);
            return (T) oi.readObject();
        }catch(IOException | ClassNotFoundException e) {
            throw new IllegalStateException("", e);
        }
    }
    
    private <T> byte[] serialize(T value)
    {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bos);
            oo.writeObject(value);
            return bos.toByteArray();
        }catch(IOException e) {
            throw new IllegalStateException("", e);
        }
    }
    
    public static void main(String[] args)
    {
        System.out.println(Serializable.class.isAssignableFrom(Protos.ExecutorInfo.class));
    }
    
}
