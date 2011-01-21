/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.db.hibernate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowfk.SnowRuntimeException;

import com.google.inject.Singleton;

@Singleton
public class HibernateHandler {

    static private Logger logger = LoggerFactory.getLogger(HibernateHandler.class);

    public enum Alert {
        ERROR_INITIALIZING_NAMING_STRATEGY_CLASS;
    }

    Map            properties;
    SessionFactory sessionFactory;
    FlushMode      flushMode = FlushMode.AUTO;

    //use a set to avoid duplicate
    Set<Class>     entityClasses = new HashSet<Class>();

    /*--------- Initialization Methods ---------*/
    /**
     * Set the properties that will be used to initialized the hibernate. Also
     * properties not startying by "hibernate." will be ignored.
     * 
     * @param properties
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /**
     * Add the entity bean classes that will be managed. Usually this methods is
     * called once per module.
     * 
     * @param clazzs
     */
    public void addEntityClasses(Class[] clazzs) {
        if (clazzs != null) {
            for (Class cls : clazzs) {
                entityClasses.add(cls);
            }
        }
    }

    /**
     * Do the heavy work of initializing hibernate and creating the
     * SessionFactory.
     * 
     * Usually called by WebApplication.init()
     */
    public void initSessionFactory(Interceptor hibernateInterceptor) {

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

        String flushModeStr = (String) properties.get("snow.hibernate.flushMode");
        if (flushModeStr != null) {
            FlushMode flushMode = FlushMode.parse(flushModeStr.toUpperCase());
            if(flushMode != null) {
                this.flushMode = flushMode;
            }
            else {
                logger.warn("unable to parse flush mode property value '" + flushModeStr + "'.  will default to " + flushMode);
            }
        }
    }

    /*--------- /Initialization Methods ---------*/

    /*--------- Session Lifecycle ---------*/
    /**
     * Open a session for the lifespan of the thread (until closeSessionInView
     * is called). This method closes any eventual session that might have been
     * already open from a previous call.
     */
    public void openSessionInView() {
        //make sure that the session is closed
        closeSessionInView();

        //get another session
        SessionHolder sessionHolder = new SessionHolder(sessionFactory, flushMode);
        SessionHolder.setThreadSessionHolder(sessionHolder);
    }

    /**
     * Flush and Close the Session(s) for the current thread. Nothing happen if
     * not session is present.<br />
     * 
     * NOTE 1: This will close the session using session.close(), which will not
     * close the db Connection. <br />
     * 
     * NOTE 2: This class call sessionHolder.flushAndClose() with might also
     * close the current transaction session.
     */
    public void closeSessionInView() {
        SessionHolder sessionHolder = SessionHolder.getThreadSessionHolder();
        if (sessionHolder != null) {
            sessionHolder.close();
        }
        SessionHolder.removeThreadSessionHolder();
    }

    /*--------- /Session Lifecycle ---------*/

}
