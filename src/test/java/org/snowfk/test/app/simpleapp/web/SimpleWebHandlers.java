package org.snowfk.test.app.simpleapp.web;

import java.util.HashMap;
import java.util.Map;


import org.snowfk.web.method.WebModelHandler;
import org.snowfk.web.method.argument.WebParam;

public class SimpleWebHandlers {

    @WebModelHandler(startsWith="/contact")
    public void contactPage(Map m, @WebParam("id")Long contactId){
        Map contact = new HashMap();
        contact.put("name","Mike");
        m.put("contact",contact);
    }
}
