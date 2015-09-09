
package org.apache.mesos.wildfly.scheduler.main;

import org.apache.mesos.wildfly.scheduler.param.SchedulerParametersMap;
import org.apache.mesos.wildfly.scheduler.param.SchedulerParameter;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import jdk.nashorn.internal.runtime.DebugLogger;
import org.apache.mesos.MesosNativeLibrary;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos.Credential;
import org.apache.mesos.Protos.FrameworkID;
import org.apache.mesos.Protos.FrameworkInfo;
import org.apache.mesos.state.InMemoryState;
import org.apache.mesos.state.State;
import org.apache.mesos.state.ZooKeeperState;
import org.apache.mesos.wildfly.common.MesosWidlflyFrameworkConfiguration;
import org.apache.mesos.wildfly.common.MesosWidlflyFrameworkConfigurationProvider;
import org.apache.mesos.wildfly.persistence.DebugPhase;
import org.apache.mesos.wildfly.persistence.SchedulerStateStore;
import org.apache.mesos.wildfly.state.IPersistentStateStore;
import org.apache.mesos.wildfly.state.MesosStateStateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class InitializationContext 
{
    
    private final Logger log = LoggerFactory.getLogger(InitializationContext.class);
    
    private IPersistentStateStore persistenceStore;    
    @Inject
    private SchedulerParametersMap schedulerParameters;
    @Inject
    private FrameWorkInitializer frameWorkInitializer;
    @Inject
    private MesosWidlflyFrameworkConfigurationProvider mesosWidlflyFrameworkConfiguration;
    
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) ServletContext init)
    {
        schedulerParameters.fillFromServletContext(init);        
        this.persistenceStore = new MesosStateStateStore(getMesosState());                
        frameWorkInitializer.connectFramework();
    }
    
    private State getMesosState()
    {
        DebugPhase phase = schedulerParameters.getEnumParameter(SchedulerParameter.DEBUG_LEVEL, DebugPhase.class);
        if(phase == DebugPhase.NONE)
        {
            MesosNativeLibrary.load(mesosWidlflyFrameworkConfiguration.getConfiguration().getNativeLibrary()); 
            //PRODUCTION ZK impl
            State mesosState = new ZooKeeperState(
                schedulerParameters.getSchedulerParameter(SchedulerParameter.ZK_SERVER), 
                schedulerParameters.getIntParameter(SchedulerParameter.ZK_TIMEOUT), TimeUnit.MILLISECONDS, 
                "mesos-wildfly/"+schedulerParameters.getSchedulerParameter(SchedulerParameter.FRAMEWORK_NAME));
            return mesosState;
        } else {
            State mesosState = new InMemoryState();
            return mesosState;
        }        
    }
    
    IPersistentStateStore getRawIPersistentStateStore()
    {
        return persistenceStore;
    }
    
    @Produces @ApplicationScoped
    public IPersistentStateStore getPersistentStateStore()
    {
        return new DelegatingPersistenceStateStore(this);
    }
        
    @Produces @ApplicationScoped
    public SchedulerStateStore getSchedulerStateStore()
    {
        return new DelegatingSchedulerStateStore(this);
    }
    
}
