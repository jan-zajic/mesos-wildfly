package org.apache.mesos.wildfly.persistence;


import org.apache.mesos.Protos;

/**
 *
 * @author jzajic
 */
public interface SchedulerStateStore
{
    
    /* concrete settings */
    void setFrameworkId(Protos.FrameworkID id);
    Protos.FrameworkID getFrameworkId();

}
