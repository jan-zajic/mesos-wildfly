
package org.apache.mesos.wildfly.executor;

import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;

/**
 *
 * @author jzajic
 */
public class WildFlyExecutor implements Executor 
{

    public void registered(ExecutorDriver driver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo)
    {
    }

    public void reregistered(ExecutorDriver driver, Protos.SlaveInfo slaveInfo)
    {
    }

    public void disconnected(ExecutorDriver driver)
    {
    }

    public void launchTask(ExecutorDriver driver, Protos.TaskInfo task)
    {
    }

    public void killTask(ExecutorDriver driver, Protos.TaskID taskId)
    {
    }

    public void frameworkMessage(ExecutorDriver driver, byte[] data)
    {
    }

    public void shutdown(ExecutorDriver driver)
    {
    }

    public void error(ExecutorDriver driver, String message)
    {
    }

}
