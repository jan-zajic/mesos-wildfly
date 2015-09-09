
package org.apache.mesos.wildfly.scheduler.main;

import org.apache.mesos.wildfly.scheduler.param.SchedulerParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import java.util.HashSet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.mesos.wildfly.scheduler.rest.RESTEasyConfigurationServices;
import org.apache.mesos.wildfly.scheduler.rest.RestApp;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.cdi.CdiPropertyInjector;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer;
import org.jboss.weld.environment.servlet.Listener;
import org.jboss.weld.util.collections.Sets;

/**
 *
 * @author jzajic
 */
public class Main 
{

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws ServletException
    {
        final Options options = createCommandLineOptions();  
                
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            new Main(cmd).start();
        } catch(ParseException parseException) {
            log.error("", parseException);
            System.err.println("Encountered exception while parsing using PosixParser:\n"+ parseException.getMessage() ); 
        }
    }
    
    private static Options createCommandLineOptions()
    {
        Options options = new Options();
        for(SchedulerParameter param : SchedulerParameter.values())
        {
            Option.Builder optionBuilder = Option
                    .builder(param.getCommandLineShort())
                    .longOpt(param.getCommandLine())
                    .hasArg()
                    .required(false);
            if(param == SchedulerParameter.ZK_SERVER)
                optionBuilder.required();
            options.addOption(optionBuilder.build());
        }
        return options;
    }
    
    private CommandLine cmd;
    
    private Main(CommandLine cmd)
    {
        this.cmd = cmd;
    }
    
    private void start() throws ServletException
    {        
        DeploymentInfo servletBuilder = Servlets.deployment();                
        servletBuilder.setClassLoader(Main.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(Main.class.getClassLoader()))
                .setContextPath("/")
                .setDeploymentName("wildfly-mesos-scheduler.war")
                .addListener(Servlets.listener(Listener.class));
        
        for(Option opt : cmd.getOptions())
        {
            String key = opt.getOpt();
            SchedulerParameter param = SchedulerParameter.forAlias(key, true);
            servletBuilder.addInitParameter(param.getLongName(), opt.getValue());
        }
        
        //viz UndertowJaxrsServer
        ServletInfo resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
              .setAsyncSupported(true)
              .setLoadOnStartup(1)
              .addMapping("/*");
                
        servletBuilder.addServlet(resteasyServlet);
        
        servletBuilder.addInitParameter("resteasy.injector.factory", CdiInjectorFactory.class.getName());
        servletBuilder.addInitParameter("javax.ws.rs.Application", RestApp.class.getName());
        
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        
        HttpHandler servletHandler = manager.start();
        
        PathHandler servletPath = Handlers.path(Handlers.redirect("/"))
                .addPrefixPath("/", servletHandler);
                //.addPrefixPath("/websocket", Handlers.websocket(filter));
        Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
                .setHandler(servletPath).build();
        server.start();
    }
    
}
