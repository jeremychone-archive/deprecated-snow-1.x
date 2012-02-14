package org.snowfk.web;

import java.io.File;
import java.util.Properties;

import org.snowfk.web.names.AppDir;
import org.snowfk.web.names.ApplicationProperties;
import org.snowfk.web.names.ApplicationFolder;
import org.snowfk.web.names.WebAppFolder;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


/**
 * Provide the root application non-overridable binding
 * 
 * - @WebAppFolder
 * - @ApplicationFolder
 * - @ApplicationPropeties
 * 
 * @author jeremychone
 *
 */
public class RootApplicationModule  extends AbstractModule {

    private File webAppFolder;
    private File appFolder;
    private Properties properties;
    
    public RootApplicationModule(Properties properties,File webAppFolder, File appFolder){
        this.webAppFolder = webAppFolder;
        this.appFolder = appFolder;   
        this.properties = properties;
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), properties);
        
        bind(Properties.class).annotatedWith(ApplicationProperties.class).toInstance(properties);
        bind(File.class).annotatedWith(WebAppFolder.class).toInstance(this.webAppFolder);
        bind(File.class).annotatedWith(ApplicationFolder.class).toInstance(this.appFolder);
        
        // for 1.2.x compatibility
        bind(File.class).annotatedWith(AppDir.class).toInstance(this.appFolder);
    }
    
}
