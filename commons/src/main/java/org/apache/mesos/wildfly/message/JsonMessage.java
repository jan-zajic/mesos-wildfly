
package org.apache.mesos.wildfly.message;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 *
 * @author jzajic
 */
@JsonTypeInfo(  
    use = JsonTypeInfo.Id.CLASS,  
    include = JsonTypeInfo.As.PROPERTY,  
    property = "type")  
public interface JsonMessage 
{

}
