package org.snowfk.test.sample1.modA;

import java.util.Map;

import org.snowfk.web.RequestContext;
import org.snowfk.web.method.WebAction;
import org.snowfk.web.method.WebModel;
import org.snowfk.web.method.WebParam;
import org.snowfk.web.method.WebUser;

public class WebBeanModels {

    @WebAction
    public String sayHello(@WebParam("fullName") String fullName){
        return "Hello " + fullName;
    }
    
    @SuppressWarnings("unchecked")
    @WebModel(startsWith="/")
    public void allPages(Map m){
        m.put("global", "toto");
    }
    
    
    
    @SuppressWarnings("unchecked")
    @WebModel(startsWith="/guestPage")
    public void guestPage(Map m,@WebParam("search") String search,@WebUser Object user, RequestContext rc){
        m.put("guest", "Alice");
        m.put("result",search + " Francisco");
    }
    
    @SuppressWarnings("unchecked")
    @WebModel(startsWith="/firstPage")
    public void firstPage(Map m,@WebParam("search") String search,RequestContext rc){
        m.put("result",search + " Francisco");
    }
    
    @SuppressWarnings("unchecked")
    @WebModel(matches="/leafPage/[0-9]*/subPage1")
    public void leafPagePage1(Map m,@WebParam("search") String search,RequestContext rc){
        m.put("subPageValue","subPage1");
    }
}
