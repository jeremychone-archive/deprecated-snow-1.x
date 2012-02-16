package org.snowfk.test.app.simpleapp.web;

import java.util.HashMap;
import java.util.Map;

import org.snowfk.util.MapUtil;
import org.snowfk.web.method.WebActionHandler;
import org.snowfk.web.method.WebModelHandler;
import org.snowfk.web.method.argument.WebParam;

public class SimpleWebHandlers {

    Map<Long,Map> contactStore = new HashMap<Long, Map>();
    Long contactIdSeq = 1L;
    
    public SimpleWebHandlers(){
        contactStore.put(contactIdSeq, MapUtil.mapIt("id",contactIdSeq,"name","Mike"));
        contactIdSeq++;
        contactStore.put(contactIdSeq, MapUtil.mapIt("id",contactIdSeq,"name","Dylan"));
        contactIdSeq++;
    }
    
    @WebModelHandler(startsWith="/contact")
    public void contactPage(Map m, @WebParam("id")Long contactId){
        Map contact = contactStore.get(contactId);
        m.put("contact",contact);
    }
    
    @WebActionHandler
    public Map addContact(@WebParam("name")String contactName){
        Map newContact = MapUtil.mapIt("id",contactIdSeq,"name",contactName);
        
        contactStore.put(contactIdSeq, MapUtil.mapIt("name",contactName));
        
        contactIdSeq++;
        
        return newContact;
        
    }
}
