
package org.apache.mesos.wildfly.scheduler.main.debug;

import org.apache.mesos.wildfly.persistence.DebugPhase;
import org.apache.mesos.wildfly.scheduler.main.Main;

/**
 *
 * @author jzajic
 */
public class DebugMain {

    public static void main(String[] args) throws Exception
    {
        Main.main(new String[]{"-zk","localhost", "-d", DebugPhase.STARTUP.name()});
    }
    
}
