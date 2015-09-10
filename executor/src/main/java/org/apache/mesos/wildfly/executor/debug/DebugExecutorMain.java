
package org.apache.mesos.wildfly.executor.debug;

import org.apache.mesos.wildfly.common.DebugPhase;
import org.apache.mesos.wildfly.executor.ExecutorMain;

/**
 *
 * @author jzajic
 */
public class DebugExecutorMain {

    public static void main(String[] args) throws Exception
    {
        ExecutorMain.main(new String[]{"-d", DebugPhase.MOCK.name(), "-p", "8081"});
    }
    
}
