
package org.apache.mesos.wildfly.message;

import java.lang.annotation.Annotation;

/**
 *
 * @author jzajic
 */
public class MessageTypeQualifierLiteral implements MessageTypeQualifier {

    private Class<? extends JsonMessage> messageType;

    public MessageTypeQualifierLiteral(Class<? extends JsonMessage> messageType)
    {
        this.messageType = messageType;
    }
    
    @Override
    public Class<? extends JsonMessage> value()
    {
        return messageType;
    }

    @Override
    public Class<? extends Annotation> annotationType()
    {
        return MessageTypeQualifier.class;
    }

    
    
}
