
package org.apache.mesos.wildfly.executor;

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
public class ExecutorParametersMap implements ExecutorParameters 
{

    private final Map<ExecutorParameter,String> paramMap = new ConcurrentHashMap<>();
    
    public ExecutorParametersMap()
    {
        for(ExecutorParameter param : ExecutorParameter.values())
        {
            paramMap.put(param, param.getDefaultValue());
        }
    }
    
    @Override
    public String getExecutorParameter(ExecutorParameter param)
    {
        return paramMap.get(param);
    }
    
    public int getIntParameter(ExecutorParameter param)
    {
        String strPar = getExecutorParameter(param);
        if(strPar == null)
            return -1;
        else
            return Integer.valueOf(strPar);
    }
    
    public <E extends Enum> E getEnumParameter(ExecutorParameter param, Class<E> enumType)
    {
        String strPar = getExecutorParameter(param);
        if(strPar == null)
            return null;
        else
            return (E) Enum.valueOf(enumType, strPar);
    }
    
    public void initFromMap(Map<String,String> externalMap)
    {
        for(ExecutorParameter param : ExecutorParameter.values())
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
            String prefix = ExecutorParameter.getLongNamePrefix();
            if(name != null && name.startsWith(prefix))
            {
                String key = name.replace(prefix, "");                
                ExecutorParameter param = ExecutorParameter.valueOf(key);
                String value = init.getInitParameter(name);
                paramMap.put(param, value);
            }
        }        
    }

}
