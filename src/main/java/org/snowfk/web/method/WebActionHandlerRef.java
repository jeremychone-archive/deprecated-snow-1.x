/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.method;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.snowfk.web.RequestContext;
import org.snowfk.web.method.argument.WebArgRef;
import org.snowfk.web.method.argument.WebParameterParser;


public class WebActionHandlerRef extends BaseWebHandlerRef{


    @SuppressWarnings("unused")
    private WebActionHandler    webAction;
    


    /*--------- Initialization ---------*/
    public WebActionHandlerRef(Object object, Method method, Map<Class<? extends Annotation>,WebParameterParser> webParameterParserMap,
                               WebActionHandler webAction) {
        super(object,method,webParameterParserMap);

        this.webAction = webAction;
        
        initWebParamRefs();
    }
    /*--------- /Initialization ---------*/
    
    
    public Object invokeWebAction(RequestContext rc) throws Exception{
        Object[] paramValues = new Object[webArgRefs.size()];
        int i = 0;
        for (WebArgRef webParamRef : webArgRefs){
            paramValues[i++] = webParamRef.getValue(method, rc);
        }
        return method.invoke(webHandler, paramValues);
    }

    
    


}
