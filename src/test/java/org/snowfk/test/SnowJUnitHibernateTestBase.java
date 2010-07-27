package org.snowfk.test;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.snowfk.web.WebApplication;
import org.snowfk.web.WebApplicationLoader;
import org.snowfk.web.WebModule;
import org.snowfk.web.db.hibernate.HibernateHandler;

public class SnowJUnitHibernateTestBase {
    //this will need to be changed to be maven friendly.
    private static String       SNOW_PATH = "WEB-INF/snow";

    static WebApplication       webApplication;
    static WebApplicationLoader appLoader;
    static HibernateHandler     hibernateHandler;

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("beforeClassTest... load & init WebApp");
        File sfkFolder = new File(SNOW_PATH);
        if (sfkFolder.exists()) {
            //assertTrue("SFK Folder does not exist", sfkFolder.exists());

            appLoader = new WebApplicationLoader(sfkFolder, null).load();

            webApplication = appLoader.getWebApplication();
            webApplication.init();
            hibernateHandler = appLoader.getHibernateHandler();
        }

    }

    @AfterClass
    public static void afterClass() throws Exception {
        System.out.println("afterClassTest... shutdown WebApp");
        if (webApplication != null) {
            ////not supported yet
            webApplication.shutdown();
            webApplication = null;
            appLoader = null;
            hibernateHandler = null;
        }
    }

    @Before
    public void beforeTestMethod() {
        if (appLoader != null) {
            //emulateRequestStart
            HibernateHandler hibernateHandler = appLoader.getHibernateHandler();
            hibernateHandler.openSessionInView();

            //inject members to this Test object (need to do for every method, since apparently there is another Test object create for each class)

            WebModule webModule = webApplication.getDefaultWebModule();
            webModule.injectMembers(this);
        }

    }

    @After
    public void afterTestMethod() {
        if (appLoader != null) {
            //emulateRequestEnd
            HibernateHandler hibernateHandler = appLoader.getHibernateHandler();
            hibernateHandler.closeSessionInView();
        }
    }

}
