
package org.apache.mesos.wildfly.scheduler;

import org.apache.mesos.wildfly.scheduler.param.SchedulerParametersMap;
import org.apache.mesos.wildfly.scheduler.param.SchedulerParameter;
import javax.inject.Inject;
import org.apache.mesos.wildfly.common.MesosWidlflyFrameworkConfiguration;
import org.apache.mesos.wildfly.common.MesosWidlflyFrameworkConfigurationProvider;

/**
 *
 * @author jzajic
 */
public class MesosWidlflyFrameworkConfigurationServer implements MesosWidlflyFrameworkConfigurationProvider
{
    
    @Inject
    private SchedulerParametersMap schedulerParameters;
    
    public String getNativeLibrary()
    {
        return schedulerParameters.getSchedulerParameter(SchedulerParameter.MESOS_NATIVE_LIB);
    }
    
    @Override
    public MesosWidlflyFrameworkConfiguration getConfiguration()
    {
        MesosWidlflyFrameworkConfiguration cfg = new MesosWidlflyFrameworkConfiguration();
        cfg.setNativeLibrary(getNativeLibrary());
        return cfg;
    }
    
}
