
package org.apache.mesos.wildfly.executor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.mesos.ExecutorDriver;
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
        ExecutorDriver driver = null;
        
        DebugPhase phase = executorParameters.getEnumParameter(ExecutorParameter.DEBUG_LEVEL, DebugPhase.class);
        if(phase == DebugPhase.NONE)
        {
            driver = new MesosExecutorDriver(wildFlyExecutor);    
        } else if(phase == DebugPhase.MOCK)
        {
            driver = TestExecutorDriver.getInstance(wildFlyExecutor);
        }
        
        if(driver != null)
            System.exit(driver.run() == Status.DRIVER_STOPPED ? 0 : 1);
        else
            System.out.println("ExecutorInitializationContext.onStartup");
    }
    
}
