
package org.apache.mesos.wildfly.executor;

import org.apache.mesos.wildfly.common.DebugPhase;

/**
 *
 * @author jzajic
 */
public enum ExecutorParameter
{

    //DEBUG PARAMETERS
    DEBUG_LEVEL(DebugPhase.NONE.name(), "debug", "d"),
    PORT("8080", "port", "p");
    
    public static ExecutorParameter forAlias(String alias, boolean enableShort)
    {
        if(alias == null)
            return null;
        for(ExecutorParameter param : ExecutorParameter.values())
        {
            if(isMatch(param, alias, enableShort))
                return param;
        }
        return null;
    }
    
    public static boolean isMatch(ExecutorParameter param, String alias, boolean enableShort)
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
        return ExecutorParameter.class.getName().toLowerCase()+".";
    }
    
    public String getLongName()
    {
        return getLongNamePrefix()+name();
    }
    
    private String defaultValue;
    private String commandLine;
    private String commandLineShort;
    
    private ExecutorParameter(String defaultValue, String commandLine, String commandLineShort)
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
