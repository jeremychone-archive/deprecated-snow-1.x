package org.snowfk.test.sample1.modA;

import java.util.Map;

import org.snowfk.web.RequestContext;
import org.snowfk.web.method.WebActionHandler;
import org.snowfk.web.method.WebModelHandler;
import org.snowfk.web.method.argument.WebParam;
import org.snowfk.web.method.argument.WebUser;

public class WebBeanModels {

    @WebActionHandler
    public String sayHello(@WebParam("fullName") String fullName){
        return "Hello " + fullName;
    }
    
    @SuppressWarnings("unchecked")
    @WebModelHandler(startsWith="/")
    public void allPages(Map m){
        m.put("global", "toto");
    }
    
    
    
    @SuppressWarnings("unchecked")
    @WebModelHandler(startsWith="/guestPage")
    public void guestPage(Map m,@WebParam("search") String search,@WebUser Object user, RequestContext rc){
        m.put("guest", "Alice");
        m.put("result",search + " Francisco");
    }
    
    @SuppressWarnings("unchecked")
    @WebModelHandler(startsWith="/firstPage")
    public void firstPage(Map m,@WebParam("search") String search,RequestContext rc){
        m.put("result",search + " Francisco");
    }
    
    @SuppressWarnings("unchecked")
    @WebModelHandler(matches="/leafPage/[0-9]*/subPage1")
    public void leafPagePage1(Map m,@WebParam("search") String search,RequestContext rc){
        m.put("subPageValue","subPage1");
    }
}
