/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.method;


import java.lang.reflect.Method;

import org.snowfk.web.RequestContext;
import org.snowfk.web.method.argument.WebArgRef;


public class WebActionHandlerRef extends BaseWebHandlerRef{


    @SuppressWarnings("unused")
    private WebActionHandler    webAction;
    


    /*--------- Initialization ---------*/
    public WebActionHandlerRef(Object object, Method method, WebActionHandler webAction) {
        super(object,method);

        this.webAction = webAction;
        
        initWebParamRefs();
    }
    /*--------- /Initialization ---------*/
    
    
    public Object invokeWebAction(RequestContext rc) throws Exception{
        Object[] paramValues = new Object[webArgRefs.size()];
        int i = 0;
        for (WebArgRef webParamRef : webArgRefs){
            paramValues[i++] = webParamRef.getValue(rc);
        }
        return method.invoke(webHandler, paramValues);
    }

    
    


}
