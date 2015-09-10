
package org.apache.mesos.wildfly.executor;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.mesos.wildfly.rest.CdiRestApp;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jzajic
 */
public class ExecutorMain {
    
    private static final Logger log = LoggerFactory.getLogger(ExecutorMain.class);
        
    public static void main(String[] args) throws Exception
    {
         final Options options = createCommandLineOptions();  
                
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            new ExecutorMain(cmd).start();
        } catch(ParseException parseException) {
            log.error("", parseException);
            System.err.println("Encountered exception while parsing using PosixParser:\n"+ parseException.getMessage() ); 
        }
        
    }
    
    private static Options createCommandLineOptions()
    {
        Options options = new Options();
        for(ExecutorParameter param : ExecutorParameter.values())
        {
            Option.Builder optionBuilder = Option
                    .builder(param.getCommandLineShort())
                    .longOpt(param.getCommandLine())
                    .hasArg()
                    .required(false);
            options.addOption(optionBuilder.build());
        }
        return options;
    }
    
    private CommandLine cmd;

    public ExecutorMain(CommandLine cmd)
    {
        this.cmd = cmd;
    }
    
    public void start() throws ServletException
    {
        DeploymentInfo servletBuilder = Servlets.deployment();                
        servletBuilder.setClassLoader(ExecutorMain.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(ExecutorMain.class.getClassLoader()))
                .setContextPath("/")
                .setDeploymentName("wildfly-mesos-scheduler.war")
                .addListener(Servlets.listener(Listener.class));
        
        Map<ExecutorParameter,String> params = new EnumMap<>(ExecutorParameter.class);
        
        for(Option opt : cmd.getOptions())
        {
            String key = opt.getOpt();            
            ExecutorParameter param = ExecutorParameter.forAlias(key, true);
            params.put(param, opt.getValue());
            servletBuilder.addInitParameter(param.getLongName(), opt.getValue());
        }
        
        //viz UndertowJaxrsServer
        ServletInfo resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
              .setAsyncSupported(true)
              .setLoadOnStartup(1)
              .addMapping("/*");
                
        servletBuilder.addServlet(resteasyServlet);
        
        servletBuilder.addInitParameter("resteasy.injector.factory", CdiInjectorFactory.class.getName());
        servletBuilder.addInitParameter("javax.ws.rs.Application", CdiRestApp.class.getName());
        
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        
        HttpHandler servletHandler = manager.start();
        
        String port = params.getOrDefault(ExecutorParameter.PORT, ExecutorParameter.PORT.getDefaultValue());        
        PathHandler servletPath = Handlers.path(Handlers.redirect("/"))
                .addPrefixPath("/", servletHandler);
        Undertow server = Undertow.builder().addHttpListener(Integer.valueOf(port), "localhost")
                .setHandler(servletPath).build();
        server.start();
    }
    
}
