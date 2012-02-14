package org.snowfk.web.db.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DefaultHibernateSessionInViewHandler implements HibernateSessionInViewHandler {
    static private Logger logger = LoggerFactory.getLogger(DefaultHibernateSessionInViewHandler.class);
    
    
    @Inject
    private SessionFactory sessionFactory;

    private FlushMode flushMode;
    
    @Inject(optional=true)
    @Named("snow.hibernate.flushMode")
    public void injectFlushMode(String flushModeStr){
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
    
    
    
    @Override
    public void openSessionInView() {
        closeSessionInView();
        
        SessionHolder sessionHolder = new SessionHolder(sessionFactory, flushMode);
        SessionHolder.setThreadSessionHolder(sessionHolder);        
        
    }

    @Override
    public void afterActionProcessing() {
        // Do nothing in the default implementation
        
    }

    @Override
    public void closeSessionInView() {
        SessionHolder sessionHolder = SessionHolder.getThreadSessionHolder();
        if (sessionHolder != null) {
            sessionHolder.close();
        }
        SessionHolder.removeThreadSessionHolder();
        
    }

}
