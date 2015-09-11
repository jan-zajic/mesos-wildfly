
package org.apache.mesos.wildfly.common;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jzajic
 */
public class UrlMesosWidlflyConfigProvider implements MesosWidlflyConfigProvider
{
    
    private final Logger log = LoggerFactory.getLogger(UrlMesosWidlflyConfigProvider.class);
    
    private URI remoteUrl;
    private URI url;
    private boolean cache = true;
    private ObjectMapper mapper;
    
    public UrlMesosWidlflyConfigProvider(URI url)
    {
        this.remoteUrl = url;
        this.mapper = new ObjectMapper();
        if(cache && url != null)
        {
            this.url = cacheUrl(remoteUrl);
        } else {
            this.url = remoteUrl;
        }
    }
    
    @Override
    public MesosWidlflyConfig getConfiguration()    
    {
        try {
            return mapper.readValue(url.toURL(), MesosWidlflyConfig.class);
        } catch(IOException e) {
            throw new IllegalStateException("", e);
        }
    }
    
    public void reload()
    {
        if(cache && url != null)
            this.url = cacheUrl(remoteUrl);
        else
            this.url = remoteUrl;
    }
    
    private URI cacheUrl(URI remoteUrl)
    {
        try {
            //create a temp file
            File temp = File.createTempFile(MesosWildFlyConstants.CONFIG_TEMP_FILE, ".tmp");
            //write it
    	    try(InputStream in = remoteUrl.toURL().openStream();
                OutputStream out = new FileOutputStream(temp);) 
            {
                ByteStreams.copy(in, out);
            }
            return temp.toURI();
        } catch(IOException e) {
            throw new IllegalStateException("", e);
        }
    }
    
}
