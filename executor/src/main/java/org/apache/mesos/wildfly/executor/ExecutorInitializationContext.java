
package org.apache.mesos.wildfly.executor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.mesos.MesosExecutorDriver;
import org.apache.mesos.Protos.Status;
import org.apache.mesos.wildfly.common.DebugPhase;
import org.apache.mesos.wildfly.executor.debug.TestExecutorDriver;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class ExecutorInitializationContext 
{
    
    @Inject    
    private WildFlyExecutor wildFlyExecutor;
    @Inject    
    private ExecutorParametersMap executorParameters;
    
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) ServletContext init)
    {
        executorParameters.fillFromServletContext(init); 
        
        DebugPhase phase = executorParameters.getEnumParameter(ExecutorParameter.DEBUG_LEVEL, DebugPhase.class);
        if(phase == DebugPhase.NONE)
        {
            MesosExecutorDriver mesosExecutorDriver = new MesosExecutorDriver(wildFlyExecutor);
            System.exit(mesosExecutorDriver.run() == Status.DRIVER_STOPPED ? 0 : 1);
        } else if(phase == DebugPhase.MOCK)
        {
            TestExecutorDriver.getInstance(wildFlyExecutor).run();
        } else {
            System.out.println("ExecutorInitializationContext.onStartup");
        }        
        
    }
    
}
