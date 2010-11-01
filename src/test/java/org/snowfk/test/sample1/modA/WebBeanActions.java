package org.snowfk.test.sample1.modA;

import java.util.Map;

import org.snowfk.util.MapUtil;
import org.snowfk.web.method.WebActionHandler;

public class WebBeanActions {

    
    @WebActionHandler
    public String sayHelloWorld(){
        return "Hello World";
    }
    
    @WebActionHandler
    public String sayHelloUsa(){
        return "Hello USA";
    }
    
    @WebActionHandler
    public Map updateEmployee(){
        return MapUtil.mapIt("firstName","mike","lastName","donavan");
    }
    
    public String thisIsNothing(){
        return null;
    }
}
