/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

import org.snowfk.util.MapUtil;
import org.snowfk.web.db.hibernate.HibernateHandler;
import org.snowfk.web.names.ServletContextPath;
import org.snowfk.web.part.PartResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class WebApplicationConfig extends AbstractModule {
   
    
    private Properties appProperties;
    private ServletContext servletContext;
    private File sfkFolder;
    
    public WebApplicationConfig(Properties appProperties,File sfkFolder,ServletContext servletContext){
        this.appProperties = appProperties;
        this.servletContext = servletContext;
        this.sfkFolder = sfkFolder;
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), appProperties);
    }
    
    @Provides
    @Singleton
    public HibernateHandler getHibernateHandler(){
        if (appProperties != null && MapUtil.hasKeyStartsWith(appProperties,"hibernate.")){
            HibernateHandler hibernateHandler = new HibernateHandler();
            hibernateHandler.setProperties(appProperties);
            return hibernateHandler;
        }else{
            return null;
        }
    }
    
    @Provides
    public ServletContext providesServletContext(){
        return servletContext;
    }
    
    @ServletContextPath
    @Provides
    @Inject
    public ContextPathFinder providesContextPathFinder(WebController webController){
        return new ServletRequestContextPathFinder(webController);
    }
    
    @Provides
    @Inject
    public CurrentRequestContextHolder providesCurrentRequestContextHolder(WebController webController){
        return webController.getCurrentRequestContextHolder();
    }
    
    @Provides
    @Inject
    public PartResolver providesPartResolver(WebApplication webApplication){
        return webApplication.getPartResolver();
    }
    
    @Named("snow.snowFolder")
    @Provides
    public File providesSnowFolder(){
        return sfkFolder;
    }

}
