
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
import io.undertow.servlet.api.ServletInfo;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.apache.mesos.wildfly.rest.CdiRestApp;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.weld.environment.servlet.Listener;
import org.jboss.weld.environment.servlet.WeldServletLifecycle;

/**
 *
 * @author jzajic
 */
public class SchedulerMain 
{

    private static final Logger log = LoggerFactory.getLogger(SchedulerMain.class);
    
    public static void main(String[] args) throws ServletException
    {
        final Options options = createCommandLineOptions();  
                
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            new SchedulerMain(cmd).start();
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
    
    private SchedulerMain(CommandLine cmd)
    {
        this.cmd = cmd;
    }
    
    private void start() throws ServletException
    {        
        DeploymentInfo servletBuilder = Servlets.deployment();                
        servletBuilder.setClassLoader(SchedulerMain.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(SchedulerMain.class.getClassLoader()))
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
        servletBuilder.addInitParameter("javax.ws.rs.Application", CdiRestApp.class.getName());
        
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        
        HttpHandler servletHandler = manager.start();
        
        PathHandler servletPath = Handlers.path(Handlers.redirect("/"))
                .addPrefixPath("/", servletHandler);
                //.addPrefixPath("/websocket", Handlers.websocket(filter));
        Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
                .setHandler(servletPath).build();
        server.start();
        
        ServletContext servletContext = manager.getDeployment().getServletContext();
        BeanManager beanManager = (BeanManager) servletContext.getAttribute(WeldServletLifecycle.BEAN_MANAGER_ATTRIBUTE_NAME);
        FrameWorkInitializer context = getBean(beanManager, FrameWorkInitializer.class);        
        SchedulerDriver driver = context.getSchedulerDriver();
        if(driver != null)
            driver.run();
        else
            System.out.println("SchedulerMain.started");
    }
    
    private <T> T getBean(BeanManager beanManager, Class<T> aClass)
    {
        Set<Bean<?>> beans = beanManager.getBeans(aClass);
        Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, aClass, creationalContext);
    }
    
}
