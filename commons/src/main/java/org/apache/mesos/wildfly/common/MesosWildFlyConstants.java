
package org.apache.mesos.wildfly.common;

/**
 *
 * @author jzajic
 */
public interface MesosWildFlyConstants
{
    
    public static final String DEFAULT_EMPTY = "";
    
    public static final String ZK_FRAMEWORK_ID_KEY = "frameworkId";
    
    public static final String CONFIG_RESOURCE_PATH = "/config";
    public static final String WILDFLY_RESOURCE_PATH = "/wildfly";
    
    public static final String STANDALONE_XML_RESOURCE_PATH = "/domain";
    public static final String HOST_XML_RESOURCE_PATH = "/host";
    
    public static final String CONFIG_TEMP_FILE = "config";
    
    public static final String CREATE_PIPE_COMMAND = "mkfifo";
    
}
