package org.snowfk.testsupport;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.snowfk.web.Application;
import org.snowfk.web.ApplicationLoader;
import org.snowfk.web.WebController;

public class SnowWebApplicationTestSupport {
    protected static String       SNOW_FOLDER_SAMPLE1_PATH = "TOOVERRIDE";

    protected static ApplicationLoader appLoader;
    protected static Application application;
    protected static WebController webController;
    
    
    /**
     * But be called by the TestUnit class from the @BeforClass method
     * @param appFolder
     * @throws Exception
     */
    public static void initWebApplication(String appFolder) throws Exception {
        File sfkFolder = new File(appFolder);
        
        assertTrue("Snow Folder " + sfkFolder.getAbsolutePath() + " does not exist", sfkFolder.exists());

        appLoader = new ApplicationLoader(sfkFolder, null).load();
        application = appLoader.getApplication();
        webController = appLoader.getWebController();
        webController.init();

        //hibernateHandler = appLoader.getHibernateHandler();
    }

    @AfterClass
    public static void releaseWebApplicaton() throws Exception {
        ////not supported yet
        //webApplication.destroy();
        application = null;
        webController = null;
        appLoader = null;
    }

    @Before
    public void emulateRequestStart() {
        if (appLoader != null) {
            /*
            HibernateHandler hibernateHandler = appLoader.getHibernateHandler();
            if (hibernateHandler != null) {
                hibernateHandler.openSessionInView();
            }
            */
        }
    }

    @After
    public void emulateRequestEnd() {
        if (appLoader != null) {
            /*
            HibernateHandler hibernateHandler = appLoader.getHibernateHandler();
            if (hibernateHandler != null) {
                hibernateHandler.closeSessionInView();
            }
            */
        }
    }

}
