package org.snowfk.test.sample1;

import static org.junit.Assert.*;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.snowfk.util.MapUtil;

import org.snowfk.web.WebActionResponse;
import org.snowfk.web.RequestContext;
import org.snowfk.web.WebController;
import org.snowfk.web.WebModule;
import org.snowfk.web.part.Part;


public class TestModA extends Sample1TestSupport{
    
    @Test
    public void alwaysPass(){
        
    }
    //@Test
    public void testWebControllerSample1(){
        try{
            
            WebController webController = appLoader.getWebController();
         
            assertNotNull("WebController is null", webController);

            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    //@Test
    public void testContentFileWithId(){
        try{
            Part part = webApplication.getPart("c:sample1.modA:[1]/test.txt");
            assertTrue("Content part does not exists : " + part.getResourceFile().getAbsolutePath(),part.getResourceFile().exists());
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    /**
     * Simple test of a JSON Post result (using the /_actionResponse.json)
     */
    //@Test
    public void testProcessPostJson(){
        try{

            Part part = webApplication.getPart("t:sample1.modA:/_actionResponse.json");
            
            RequestContext rc = new RequestContext(part,null);
            
            WebActionResponse webActionResponse = webApplication.processWebAction("sample1.modA", "updateEmployee", rc);
            rc.setWebActionResponse(webActionResponse);
            
            StringWriter sw = new StringWriter();
            rc.setWriter(sw);
            webApplication.processJsonPart(part, rc);
            assertEquals("{\"error\":null,\"errorMessage\":\"\",\"errorType\":null,\"result\":{\"lastName\":\"donavan\",\"firstName\":\"mike\"},\"status\":\"success\"}",sw.toString());
            
            
            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    //@Test
    public void testMatchesRegexWebModel(){
        try{
            Part part = webApplication.getPart("t:sample1.modA:/leafPage/123/subPage1");
            RequestContext rc = new RequestContext(part, null);
            StringWriter sw = new StringWriter();
            rc.setWriter(sw);
            webApplication.processFreemarkerPart(part, rc);
            assertEquals("This is the leafPage.ftl subPage1",sw.toString());     
            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    //@Test
    public void testProcessModAPart(){
        try{

            
            //test the first page
            Part part = webApplication.getPart("t:sample1.modA:/firstPage");
            RequestContext rc = new RequestContext(part, (Map<String, Object>) MapUtil.mapIt("fullName", "John","search","San"));
            StringWriter sw = new StringWriter();
            rc.setWriter(sw);
            webApplication.processFreemarkerPart(part, rc);
            assertEquals("This is the firstPage toto San Francisco",sw.toString());
            
            //test a sub page
            part = webApplication.getPart("t:sample1.modA:/sub/secondPage");
            rc = new RequestContext(part, (Map<String, Object>) MapUtil.mapIt("fullName", "John","search","San"));
            sw = new StringWriter();
            rc.setWriter(sw);
            webApplication.processFreemarkerPart(part, rc);
            assertEquals("This is the sub/secondPage.ftl",sw.toString());
   
            //get a leafPage
            part = webApplication.getPart("t:sample1.modA:/leafPage");
            rc = new RequestContext(part, (Map<String, Object>) MapUtil.mapIt("fullName", "John","search","San"));
            sw = new StringWriter();
            rc.setWriter(sw);
            webApplication.processFreemarkerPart(part, rc);
            assertEquals("This is the leafPage.ftl",sw.toString());            
            
            //get a leafPage
            part = webApplication.getPart("t:sample1.modA:/leafPage/subSection");
            rc = new RequestContext(part, (Map<String, Object>) MapUtil.mapIt("fullName", "John","search","San"));
            sw = new StringWriter();
            rc.setWriter(sw);
            webApplication.processFreemarkerPart(part, rc);
            assertEquals("This is the leafPage.ftl",sw.toString());       
            
            
        }catch(Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    //@Test    
    public void testApplicationLoaderSample1() {
        

        try {


            /*--------- Check the WebModule Names ---------*/
            assertEquals("Application.defaultModuleName", "sample1.modA", webApplication.getDefaultWebModuleName());
            WebModule modA = webApplication.getWebModule("sample1.modA");
            assertEquals("sample1.modA", modA.getName());

            WebModule modB = webApplication.getWebModule("sample1.modB");
            assertEquals("sample1.modB", modB.getName());
            
            WebModule modC = webApplication.getWebModule("sample1.modC");
            assertEquals("sample1.modC", modC.getName());
            
            Part part = webApplication.getPart("t:sample1.modA:/guestPage");
            RequestContext rc = new RequestContext(part, (Map<String, Object>) MapUtil.mapIt("fullName", "John","search","San"));

            /*--------- /Check the WebModule Names ---------*/
            
            
            //// test WebActions
            
            assertEquals("Hello World", webApplication.processWebAction("sample1.modA", "sayHelloWorld", rc).getResult());
            assertEquals("Hello USA", webApplication.processWebAction("sample1.modA", "sayHelloUsa", rc).getResult());
            assertEquals("Hello John", webApplication.processWebAction("sample1.modA", "sayHello", rc).getResult());

            //// test the WebModels
            Map model = new HashMap();
            webApplication.proccessWebModels("sample1.modA", model, rc);
            assertEquals("Alice", "" + model.get("guest"));
            assertEquals("San Francisco", "" + model.get("result"));
            assertEquals("toto", "" + model.get("global"));
            
            //// test parts
            part = webApplication.getPart("t:sample1.modA:/firstPage.ftl");
            assertTrue("Part /firstPage.ftl",part.getResourceFile().exists());
            assertEquals("Part type",Part.Type.t,part.getType());
            assertEquals("Part formatType",Part.FormatType.freemarker,part.getFormatType());
            
            part = webApplication.getPart("t:sample1.modA:/firstPage");
            assertTrue("Part /firstPage does not exist",part.getResourceFile().exists());
            assertEquals("Part type",Part.Type.t,part.getType());
            assertEquals("Part formatType",Part.FormatType.freemarker,part.getFormatType());
            
            part = webApplication.getPart("t:sample1.modA:/sample1.css");
            assertTrue("Part /firstPage does not exist",part.getResourceFile().exists());
            assertEquals("Part type",Part.Type.t,part.getType());
            assertEquals("Part formatType",Part.FormatType.text,part.getFormatType());
            
            part = webApplication.getPart("t:sample1.modA:/sub/secondPage");
            assertTrue("Part /secondPage does not exist",part.getResourceFile().exists());
            assertEquals("Part type",Part.Type.t,part.getType());
            assertEquals("Part formatType",Part.FormatType.freemarker,part.getFormatType());
            
            part = webApplication.getPart("t:sample1.modA:/sub/some.xml");
            assertTrue("Part /secondPage does not exist",part.getResourceFile().exists());
            assertEquals("Part type",Part.Type.t,part.getType());
            assertEquals("Part formatType",Part.FormatType.text,part.getFormatType());
            
            //// test content folder
            assertNotNull("contentFolder",webApplication.getContentFolder());
            assertTrue("contentFolder does not exists: " + webApplication.getContentFolder().getAbsolutePath(),webApplication.getContentFolder().exists());
            
            part = webApplication.getPart("c:sample1.modA:/content1.xml");
            assertTrue("Part c:sample1.modA:/content1.xml does not exist",part.getResourceFile().exists());
            assertEquals("Part type",Part.Type.c,part.getType());
            assertEquals("Part formatType",Part.FormatType.text,part.getFormatType());
            
            

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    


}
