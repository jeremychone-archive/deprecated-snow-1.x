/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.util.Properties;

import javax.servlet.ServletContext;


import org.snowfk.annotation.Nullable;
import org.snowfk.util.MapUtil;
import org.snowfk.web.db.hibernate.HibernateHandler;
import org.snowfk.web.names.AppDir;
import org.snowfk.web.names.CurrentRequestContext;
import org.snowfk.web.names.ServletContextPath;
import org.snowfk.web.part.PartResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class WebApplicationConfig extends AbstractModule {

	private Properties appProperties;
	private ServletContext servletContext;
	private File sfkFolder;
	private File appDir;

	public WebApplicationConfig(Properties appProperties, File sfkFolder,File appDir, ServletContext servletContext) {
		this.appProperties = appProperties;
		this.servletContext = servletContext;
		this.sfkFolder = sfkFolder;
		this.appDir = appDir;
	}

	@Override
	protected void configure() {
		Names.bindProperties(binder(), appProperties);
	}

	@Provides
	@Singleton
	public HibernateHandler getHibernateHandler() {
		if (appProperties != null && MapUtil.hasKeyStartsWith(appProperties, "hibernate.")) {
			HibernateHandler hibernateHandler = new HibernateHandler();
			hibernateHandler.setProperties(appProperties);
			return hibernateHandler;
		} else {
			return null;
		}
	}

	@Provides
	public ServletContext providesServletContext() {
		return servletContext;
	}
	
	@AppDir
	@Provides
	public File providesAppDir(){
		return appDir;
	}

	@ServletContextPath
	@Provides
	@Inject
	public String providesContextPathFinder(@Nullable @CurrentRequestContext RequestContext requestContext) {
		if (requestContext != null) {
			return requestContext.getContextPath();
		}
		return null;
	}

	@CurrentRequestContext
	@Provides
	@Inject
	public RequestContext providesCurrentRequestContext(@Nullable CurrentRequestContextHolder rcHolder) {
		if (rcHolder != null) {
			return rcHolder.getCurrentRequestContext();
		}
		return null;
	}

	@Provides
	@Inject
	public CurrentRequestContextHolder providesCurrentRequestContextHolder(WebController webController) {
		return webController.getCurrentRequestContextHolder();
	}

	@Provides
	@Inject
	public PartResolver providesPartResolver(WebApplication webApplication) {
		return webApplication.getPartResolver();
	}
	

	@Named("snow.snowFolder")
	@Provides
	public File providesSnowFolder() {
		return sfkFolder;
	}

}
