/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.snowfk.web.db.hibernate.HibernateDaoHelper;
import org.snowfk.web.db.hibernate.HibernateDaoHelperImpl;
import org.snowfk.web.names.EntityClasses;
import org.snowfk.web.names.WebHandlerClasses;
import org.snowfk.web.names.WebHandlers;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class WebModuleConfig extends AbstractModule {

    private Properties     properties;
    private WebApplication webApplication;

    protected void configure() {
        if (properties != null) {
            Names.bindProperties(binder(), properties);
        }
        bind(WebApplication.class).toInstance(webApplication);        
    }

    /**
     * To be overridden by the WebModules to provide the module entity classes
     * to be managed by the ORM (for now Hibernate only)
     * 
     * @return a array of Class annotated with @Entity (for Hibernate). Must
     *         return empty list if now beans.
     */
    @Provides
    @EntityClasses
    public Class[] provideEntityClasses() {
        return new Class[0];
    }

    /**
     * To be overridden to list the WebHandler objects for a module. Easiest way
     * to configure the WebHandlers. <br>
     * Alternatively, override "provideWebHandlers" and generated the instance directly for the
     * WebApplicationLoader to add those classes to the HibernateHandler.
     */
    @Provides
    @Singleton
    @WebHandlerClasses
    public Class[] provideWebHandlerClasses() {
        return new Class[0];
    }

    @SuppressWarnings("unchecked")
    @Provides
    @Singleton
    @WebHandlers
    @Inject
    public List<Object> provideWebHandlers(@WebHandlerClasses Class[] webBeanClasses, Injector injector) {
        List<Object> webBeans = new ArrayList<Object>();
        if (webBeanClasses != null) {
            for (Class webBeanClass : webBeanClasses) {
                Object webBean = injector.getInstance(webBeanClass);
                webBeans.add(webBean);
            }
        }
        return webBeans;
    }

    @Provides
    @Inject
    @Singleton
    public HibernateDaoHelper provideHibernateDaoHelper(HibernateDaoHelperImpl impl){
        return impl;
    }

    /*--------- Set by WebApplicationLoader ---------*/
    /**
     * Must be called before creating the injector. This is usually call by
     * WebApplicationLoader.load() for each WebModule.<br>
     * TODO: might want to add some security, such not every module get this.
     * 
     * @param webApplication
     */
    void setWebApplication(WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    void setProperties(Properties properties) {
        this.properties = properties;
    }
    /*--------- /Set by WebApplicationLoader ---------*/

}
