
package org.apache.mesos.wildfly.scheduler.param;

import static org.apache.mesos.wildfly.common.MesosWildFlyConstants.*;
import org.apache.mesos.wildfly.persistence.DebugPhase;

/**
 *
 * @author jzajic
 */
public enum SchedulerParameter
{
    
    //MESOS FRAMEWORK NAME
    FRAMEWORK_NAME("wildfly", "name", "n"), 
    //ZOOKEPER
    ZK_SERVER(DEFAULT_EMPTY, "zk.servers", "zk"), ZK_TIMEOUT(DEFAULT_EMPTY, "zk.timeout", "zkt"),
    //DEBUG PARAMETERS
    DEBUG_LEVEL(DebugPhase.NONE.name(), "debug", "d"),
    //path to native library mesos.so
    MESOS_NATIVE_LIB("/usr/local/lib/libmesos.so", "mesos.native.lib", "nl");    
    
    public static SchedulerParameter forAlias(String alias, boolean enableShort)
    {
        if(alias == null)
            return null;
        for(SchedulerParameter param : SchedulerParameter.values())
        {
            if(isMatch(param, alias, enableShort))
                return param;
        }
        return null;
    }
    
    public static boolean isMatch(SchedulerParameter param, String alias, boolean enableShort)
    {
        if(alias.equalsIgnoreCase(param.commandLine))
            return true;
        else if(enableShort && alias.equalsIgnoreCase(param.commandLineShort))
            return true;
        else
            return false;
    }

    public static String getLongNamePrefix()
    {
        return SchedulerParameter.class.getName().toLowerCase()+".";
    }
    
    public String getLongName()
    {
        return getLongNamePrefix()+name();
    }
    
    private String defaultValue;
    private String commandLine;
    private String commandLineShort;
    
    private SchedulerParameter(String defaultValue, String commandLine, String commandLineShort)
    {
        this.defaultValue = defaultValue;
        this.commandLine = commandLine;
        this.commandLineShort = commandLineShort;                 
    }
    
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getCommandLine()
    {
        return commandLine;
    }

    public String getCommandLineShort()
    {
        return commandLineShort;
    }
    
}
