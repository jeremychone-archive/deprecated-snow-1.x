package org.snowfk.test.simpleapp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.snowfk.test.SnowWebApplicationTestSupport;
import org.snowfk.web.WebController;

public class SimpleAppTest extends SnowWebApplicationTestSupport {

    @BeforeClass
    public static void initTestClass() throws Exception{
        SnowWebApplicationTestSupport.initWebApplication( "src/test/resources/simpleApp");
    }
    
    @Test
    public void testWebController(){
        try{
            
            WebController webController = appLoader.getWebController();
         
            assertNotNull("WebController is null", webController);

            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
}
