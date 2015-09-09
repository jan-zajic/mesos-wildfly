
package org.apache.mesos.wildfly.scheduler.param;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class SchedulerParametersMap implements SchedulerParameters 
{

    private final Map<SchedulerParameter,String> paramMap = new ConcurrentHashMap<>();
    
    public SchedulerParametersMap()
    {
        for(SchedulerParameter param : SchedulerParameter.values())
        {
            paramMap.put(param, param.getDefaultValue());
        }
    }
    
    @Override
    public String getSchedulerParameter(SchedulerParameter param)
    {
        return paramMap.get(param);
    }
    
    public int getIntParameter(SchedulerParameter param)
    {
        String strPar = getSchedulerParameter(param);
        if(strPar == null)
            return -1;
        else
            return Integer.valueOf(strPar);
    }
    
    public <E extends Enum> E getEnumParameter(SchedulerParameter param, Class<E> enumType)
    {
        String strPar = getSchedulerParameter(param);
        if(strPar == null)
            return null;
        else
            return (E) Enum.valueOf(enumType, strPar);
    }
    
    public void initFromMap(Map<String,String> externalMap)
    {
        for(SchedulerParameter param : SchedulerParameter.values())
        {
            if(externalMap.containsKey(param.getCommandLine()))
                paramMap.put(param, externalMap.get(param.getCommandLine()));
        }
    }
    
    public void fillFromServletContext(ServletContext init)
    {
        Enumeration<String> initParameterNames = init.getInitParameterNames();
        while(initParameterNames.hasMoreElements())
        {
            String name = initParameterNames.nextElement();
            String prefix = SchedulerParameter.getLongNamePrefix();
            if(name != null && name.startsWith(prefix))
            {
                String key = name.replace(prefix, "");                
                SchedulerParameter param = SchedulerParameter.valueOf(key);
                String value = init.getInitParameter(name);
                paramMap.put(param, value);
            }
        }        
    }

}
