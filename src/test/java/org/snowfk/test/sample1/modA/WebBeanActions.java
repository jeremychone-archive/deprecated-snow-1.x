package org.snowfk.test.sample1.modA;

import java.util.Map;

import org.snowfk.util.MapUtil;
import org.snowfk.web.method.WebAction;

public class WebBeanActions {

    
    @WebAction
    public String sayHelloWorld(){
        return "Hello World";
    }
    
    @WebAction
    public String sayHelloUsa(){
        return "Hello USA";
    }
    
    @WebAction
    public Map updateEmployee(){
        return MapUtil.mapIt("firstName","mike","lastName","donavan");
    }
    
    public String thisIsNothing(){
        return null;
    }
}
