package org.snowfk.test.simpleapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.snowfk.testsupport.SnowWebApplicationTestSupport;
import org.snowfk.testsupport.mock.MockFactory;
import org.snowfk.testsupport.mock.MockFactory.RequestMethod;
import org.snowfk.testsupport.mock.RequestContextMock;
import org.snowfk.util.MapUtil;

public class SimpleAppTest extends SnowWebApplicationTestSupport {

    @BeforeClass
    public static void initTestClass() throws Exception{
        SnowWebApplicationTestSupport.initWebApplication( "src/test/resources/simpleApp");
    }
    
    @Test
    public void testHelloPage(){
        try{

            MockFactory mockFactory = new MockFactory().init();
            RequestContextMock rc = mockFactory.createRequestContext(RequestMethod.GET,"/helloPage");
            Map<String,Object> paramMap = (Map<String, Object>) MapUtil.mapIt("name","John");
            rc.setParamMap(paramMap);
            
            webController.service(rc);
            
            String result = rc.getResponseAsString();
            
            assertEquals("---Hello John---",result);

            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testContactJson(){
        try{

            MockFactory mockFactory = new MockFactory().init();
            RequestContextMock rc = mockFactory.createRequestContext(RequestMethod.GET,"/contact.json");
            Map<String,Object> paramMap = (Map<String, Object>) MapUtil.mapIt("id","123");
            rc.setParamMap(paramMap);
            
            webController.service(rc);
            
            String result = rc.getResponseAsString();
            
            System.out.println("Result: " + result);
            //assertEquals("---Hello John---",result);

            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }        
    }
    
}
