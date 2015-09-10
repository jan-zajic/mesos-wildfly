
package org.apache.mesos.wildfly.common;

/**
 *
 * @author jzajic
 */
public class MesosWidlflyConfig 
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
