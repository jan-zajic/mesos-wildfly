
package org.apache.mesos.wildfly.scheduler.main;

import org.apache.mesos.wildfly.scheduler.param.SchedulerParametersMap;
import org.apache.mesos.wildfly.scheduler.param.SchedulerParameter;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.apache.mesos.wildfly.common.DebugPhase;
import org.apache.mesos.wildfly.persistence.SchedulerStateStore;
import org.apache.mesos.wildfly.scheduler.WildFlyScheduler;
import org.apache.mesos.wildfly.scheduler.main.debug.TestSchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class FrameWorkInitializer {

    private final Logger log = LoggerFactory.getLogger(FrameWorkInitializer.class);
    
    @Inject
    private SchedulerStateStore stateStore;
    @Inject
    private WildFlyScheduler scheduler;
    @Inject
    private SchedulerParametersMap schedulerParameters;
    
    private SchedulerDriver schedulerDriver;
    
    void connectFramework()
    {
        Protos.FrameworkInfo.Builder frameworkInfo = Protos.FrameworkInfo.newBuilder()
            .setName("mesos-wildfly")
            .setFailoverTimeout(100)
            .setUser("root")
            .setRole("")
            .setCheckpoint(true);
        
        Protos.FrameworkID frameworkID = stateStore.getFrameworkId();
        if (frameworkID != null) {
            frameworkInfo.setId(frameworkID);
        }
        
        registerFramework(scheduler, frameworkInfo.build(), getMesosMasterUri());
    }
    
    private void registerFramework(WildFlyScheduler scheduler, Protos.FrameworkInfo fInfo, String masterUri) 
    {
      DebugPhase debugPhase = schedulerParameters.getEnumParameter(SchedulerParameter.DEBUG_LEVEL, DebugPhase.class);
      if(debugPhase == DebugPhase.STARTUP)
      {
          System.out.println("CALLED InitializationContext.registerFramework with masterUri: "+masterUri);  
      }
      else if(debugPhase == DebugPhase.MOCK)
      {
        this.schedulerDriver = TestSchedulerDriver.getInstance(scheduler);  
      } else {
        Protos.Credential cred = getCredential();

        if (cred != null) {
          log.info("Registering with credentials.");
          this.schedulerDriver = new MesosSchedulerDriver(scheduler, fInfo, masterUri, cred);
        } else {
          log.info("Registering without authentication");
          this.schedulerDriver = new MesosSchedulerDriver(scheduler, fInfo, masterUri);
        }
      }
    }
    
    private Protos.Credential getCredential() 
    {
        //TODO: handle credentials
        return null;
    }
    
    private String getMesosMasterUri()    
    {
        //TODO: handle credentials
        return "zk://"+schedulerParameters.getSchedulerParameter(SchedulerParameter.ZK_SERVER)+"/mesos";
    }

    public SchedulerDriver getSchedulerDriver()
    {
        return schedulerDriver;
    }
        
}
