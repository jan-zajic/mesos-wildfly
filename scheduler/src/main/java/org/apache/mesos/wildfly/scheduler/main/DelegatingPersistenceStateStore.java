
package org.apache.mesos.wildfly.scheduler.main;

import org.apache.mesos.wildfly.scheduler.main.InitializationContext;
import org.apache.mesos.wildfly.state.IPersistentStateStore;

/**
 *
 * @author jzajic
 */
public class DelegatingPersistenceStateStore implements IPersistentStateStore 
{

    private InitializationContext initializationContext;

    public DelegatingPersistenceStateStore(InitializationContext initializationContext)
    {
        this.initializationContext = initializationContext;
    }
    
    @Override
    public byte[] getRawValueForId(String id)
    {
        return getDelegate().getRawValueForId(id);
    }

    @Override
    public void setRawValueForId(String id, byte[] frameworkId)
    {
        getDelegate().setRawValueForId(id, frameworkId);
    }

    @Override
    public <T> T get(String key)
    {
        return getDelegate().get(key);
    }

    @Override
    public <T> void set(String key, T object)
    {
        getDelegate().set(key, object);
    }

    private IPersistentStateStore getDelegate()
    {
        IPersistentStateStore store = initializationContext.getRawIPersistentStateStore();
        if(store == null)
            throw new IllegalStateException("cannot access IPersistentStateStore - not initialized yet");
        else
            return store;
    }
    
}
