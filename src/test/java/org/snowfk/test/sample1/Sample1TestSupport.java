/*
 * Copyright 2009 Jeremy Chone
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.snowfk.test.sample1;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.snowfk.web.WebApplication;
import org.snowfk.web.WebApplicationLoader;
import org.snowfk.web.db.hibernate.HibernateHandler;

public class Sample1TestSupport {
    private static String       SNOW_FOLDER_SAMPLE1_PATH = "src/test/resources/sample1";

    static WebApplication       webApplication;
    static WebApplicationLoader appLoader;
    static HibernateHandler     hibernateHandler;

    @BeforeClass
    public static void initWebApplication() throws Exception {
        File sfkFolder = new File(SNOW_FOLDER_SAMPLE1_PATH);
        assertTrue("SFK Folder does not exist", sfkFolder.exists());

        appLoader = new WebApplicationLoader(sfkFolder, null).load();

        webApplication = appLoader.getWebApplication();
        webApplication.init();
        hibernateHandler = appLoader.getHibernateHandler();
    }

    @AfterClass
    public static void releaseWebApplicaton() throws Exception {
        ////not supported yet
        //webApplication.destroy();
        webApplication = null;
        appLoader = null;
        hibernateHandler = null;
    }

    @Before
    public void emulateRequestStart() {
        if (appLoader != null) {
            HibernateHandler hibernateHandler = appLoader.getHibernateHandler();
            if (hibernateHandler != null) {
                hibernateHandler.openSessionInView();
            }
        }
    }

    @After
    public void emulateRequestEnd() {
        if (appLoader != null) {
            HibernateHandler hibernateHandler = appLoader.getHibernateHandler();
            if (hibernateHandler != null) {
                hibernateHandler.closeSessionInView();
            }
        }
    }

}
