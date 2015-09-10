
package org.apache.mesos.wildfly.scheduler.main.debug;

import org.apache.mesos.wildfly.common.DebugPhase;
import org.apache.mesos.wildfly.scheduler.main.SchedulerMain;

/**
 *
 * @author jzajic
 */
public class DebugSchedulerMain {

    public static void main(String[] args) throws Exception
    {
        SchedulerMain.main(new String[]{"-zk","localhost", "-d", DebugPhase.MOCK.name()});
    }
    
}
