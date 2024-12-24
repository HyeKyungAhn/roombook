package store.roombook.global.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

public class WebApplicationInitializerImpl implements WebApplicationInitializer {
    private static final int MAX_FILE_SIZE = 2097152;
    private static final int MAX_REQUEST_SIZE = 5000000;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // root context configuration
        servletContext.setInitParameter("contextConfigLocation", "/WEB-INF/spring/applicationContext.xml");

        XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
        rootContext.setConfigLocation("/WEB-INF/spring/applicationContext.xml");

        servletContext.addListener(new ContextLoaderListener(rootContext));

        //servlet context configuration
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/spring/dispatcher-servlet.xml");
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/resources/uploads/**", MAX_FILE_SIZE, MAX_REQUEST_SIZE, 0);
        dispatcher.setMultipartConfig(multipartConfigElement);

        //Filter
        FilterRegistration.Dynamic forwardedHeaderFilter = servletContext.addFilter("forwardedHeaderFilter", ForwardedHeaderFilter.class);
        forwardedHeaderFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");
        forwardedHeaderFilter.setAsyncSupported(true);

        //encoding
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        FilterRegistration.Dynamic characterEncoding = servletContext.addFilter("characterEncodingFilter", characterEncodingFilter);
        characterEncoding.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        //security
        DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy();
        FilterRegistration.Dynamic springSecurity = servletContext.addFilter("springSecurityFilterChain", springSecurityFilterChain);
        springSecurity.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST),true,"/*");

        servletContext.setInitParameter("log4jConfiguration", "file:/src/resources/log4j2.xml");
    }
}
