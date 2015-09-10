
package org.apache.mesos.wildfly.scheduler;

import org.apache.mesos.wildfly.scheduler.param.SchedulerParametersMap;
import org.apache.mesos.wildfly.scheduler.param.SchedulerParameter;
import javax.inject.Inject;
import org.apache.mesos.wildfly.common.MesosWidlflyConfig;
import org.apache.mesos.wildfly.common.MesosWidlflyConfigProvider;

/**
 *
 * @author jzajic
 */
public class MesosWidlflyFrameworkConfigurationServer implements MesosWidlflyConfigProvider
{
    
    @Inject
    private SchedulerParametersMap schedulerParameters;
    
    public String getNativeLibrary()
    {
        return schedulerParameters.getSchedulerParameter(SchedulerParameter.MESOS_NATIVE_LIB);
    }
    
    @Override
    public MesosWidlflyConfig getConfiguration()
    {
        MesosWidlflyConfig cfg = new MesosWidlflyConfig();
        cfg.setNativeLibrary(getNativeLibrary());
        return cfg;
    }
    
}
