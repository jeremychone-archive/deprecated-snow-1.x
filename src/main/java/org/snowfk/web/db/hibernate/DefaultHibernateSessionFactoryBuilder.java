package org.snowfk.web.db.hibernate;

import java.util.Properties;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.snowfk.SnowRuntimeException;
import org.snowfk.web.Initializable;
import org.snowfk.web.db.hibernate.HibernateHandler.Alert;
import org.snowfk.web.names.ApplicationProperties;
import org.snowfk.web.names.EntityClasses;

import com.google.inject.Inject;

public class DefaultHibernateSessionFactoryBuilder implements HibernateSessionFactoryBuilder, Initializable {

    private SessionFactory sessionFactory;
    
    @Inject
    private @ApplicationProperties Properties properties;

    @Inject(optional=true)
    private @HibernateInterceptorBinding Interceptor hibernateInterceptor;
    
    @Inject
    private @EntityClasses Class[] entityClasses;
    
    @Override
    public void init() {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        for (Class cls : entityClasses) {
            cfg.addAnnotatedClass(cls);
        }

        //set the hibernate properties
        for (Object key : properties.keySet()) {
            String keyStr = key.toString();
            if (keyStr.startsWith("hibernate.")) {
                String value = properties.get(key).toString();
                cfg.setProperty(keyStr, value);
            }
        }

        //get the eventual namingStrategy
        String namingStrategyClassStr = (String) properties.get("snow.hibernate.namingStrategyClass");
        if (namingStrategyClassStr != null) {
            Class namingStrategyClass;
            try {
                namingStrategyClass = Class.forName(namingStrategyClassStr);
                NamingStrategy namingStrategy = (NamingStrategy) namingStrategyClass.newInstance();
                cfg.setNamingStrategy(namingStrategy);
            } catch (Exception e) {
                throw new SnowRuntimeException(Alert.ERROR_INITIALIZING_NAMING_STRATEGY_CLASS,e, "namingStrategyClass",
                                        namingStrategyClassStr);
            }

        }

        if (hibernateInterceptor != null) {
            cfg.setInterceptor(hibernateInterceptor);
        }

        sessionFactory = cfg.buildSessionFactory();
        
    }
    
    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


}
