package org.snowfk.test.simpleapp;

import static org.junit.Assert.assertEquals;
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
    public static void initTestClass() throws Exception {
        SnowWebApplicationTestSupport.initWebApplication("src/test/resources/simpleApp");
    }

    @Test
    public void testHelloPage() {
        try {

            MockFactory mockFactory = new MockFactory().init();
            RequestContextMock rc = mockFactory.createRequestContext(RequestMethod.GET, "/helloPage");
            Map<String, Object> paramMap = (Map<String, Object>) MapUtil.mapIt("name", "John");
            rc.setParamMap(paramMap);

            webController.service(rc);

            String result = rc.getResponseAsString();

            assertEquals("---Hello John---", result);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testContactJson() {
        try {
            Map result;
            RequestContextMock rc;
            MockFactory mockFactory = new MockFactory().init();

            // test getting contact id = 1 (Mike)
            rc = mockFactory.createRequestContext(RequestMethod.GET, "/contact.json");
            rc.setParamMap(MapUtil.mapIt("id", "1"));
            webController.service(rc);
            result = rc.getResponseAsJson();
            assertEquals("Mike",MapUtil.getNestedValue(result, "contact.name"));

            // test getting contact id = 2 (Dylan)
            rc = mockFactory.createRequestContext(RequestMethod.GET, "/contact.json");
            rc.setParamMap(MapUtil.mapIt("id", "2"));
            webController.service(rc);
            result = rc.getResponseAsJson();
            assertEquals("Dylan",MapUtil.getNestedValue(result, "contact.name"));

        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testContactPage() {
        try {
            String result;
            RequestContextMock rc;
            MockFactory mockFactory = new MockFactory().init();
            
            // test with the /contact path
            rc = mockFactory.createRequestContext(RequestMethod.GET, "/contact");
            rc.setParamMap(MapUtil.mapIt("id", "1"));
            webController.service(rc);
            result = rc.getResponseAsString();
            assertEquals("---Hello Mike---", result);
            
            // test with the /contact.ftl path 
            rc = mockFactory.createRequestContext(RequestMethod.GET, "/contact.ftl");
            rc.setParamMap(MapUtil.mapIt("id", "1"));
            webController.service(rc);
            result = rc.getResponseAsString();
            assertEquals("---Hello Mike---", result);
            
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    
    @Test
    public void testNotesIndexPage() {
        try {
            String result;
            RequestContextMock rc;
            MockFactory mockFactory = new MockFactory().init();
            
            // test with the /contact path
            rc = mockFactory.createRequestContext(RequestMethod.GET, "notes/");
            webController.service(rc);
            result = rc.getResponseAsString();
            assertEquals("---This is the notes/index.ftl page---",result);

        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }   
    
    @Test
    public void testAddContactAction() {
        try {
            Map result;
            RequestContextMock rc;
            MockFactory mockFactory = new MockFactory().init();
            
            // test add contact
            rc = mockFactory.createRequestContext(RequestMethod.POST, "/_actionResponse.json");
            rc.setParamMap(MapUtil.mapIt("action","addContact","name", "Jennifer"));
            webController.service(rc);
            result = rc.getResponseAsJson();
            assertEquals(MapUtil.getNestedValue(result, "result.name"),"Jennifer");

            
            String newContactId = MapUtil.getNestedValue(result, "result.id");
            rc = mockFactory.createRequestContext(RequestMethod.GET, "/contact.json");
            rc.setParamMap(MapUtil.mapIt("id",newContactId));
            webController.service(rc);
            result = rc.getResponseAsJson();
            assertEquals("Jennifer",MapUtil.getNestedValue(result, "contact.name"));
            

        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }      

}
