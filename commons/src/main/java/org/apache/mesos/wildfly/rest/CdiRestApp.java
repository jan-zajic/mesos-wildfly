
package org.apache.mesos.wildfly.rest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Application;

/**
 *
 * @author jzajic
 */
@ApplicationScoped
public class CdiRestApp extends Application
{

    @Inject
    private Instance<RestService> services;
        
    @Override
    public Set<Object> getSingletons()
    {
        Set<Object> set = new HashSet<>();
        for (Iterator<RestService> iterator = services.iterator(); iterator.hasNext();) {
            RestService next = iterator.next();
            set.add(next);
        }
        return set;
    }
            
}
