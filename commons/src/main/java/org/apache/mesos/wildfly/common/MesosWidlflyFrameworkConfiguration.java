
package org.apache.mesos.wildfly.common;

/**
 *
 * @author jzajic
 */
public class MesosWidlflyFrameworkConfiguration 
{

    private String nativeLibrary;

    public String getNativeLibrary()
    {
        return nativeLibrary;
    }

    public void setNativeLibrary(String nativeLibrary)
    {
        this.nativeLibrary = nativeLibrary;
    }
    
}
