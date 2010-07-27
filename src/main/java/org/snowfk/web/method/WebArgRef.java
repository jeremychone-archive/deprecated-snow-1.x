/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.method;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.snowfk.web.RequestContext;

public class WebArgRef {

    private WebParam  webParam  = null;
    private WebUser   webUser   = null;
    private WebPath   webPath   = null;
    private WebMap    webMap    = null;
    private WebEntity webEntity = null;
    private WebState  webState  = null;

    private Class     paramClass;

    public WebArgRef(Class paramClass) {
        this.paramClass = paramClass;
    }

    public WebArgRef(WebParam webParam, Class paramClass) {
        this(paramClass);
        this.webParam = webParam;
    }

    public WebArgRef(WebUser webUser, Class paramClass) {
        this(paramClass);
        this.webUser = webUser;
    }

    public WebArgRef(WebPath webPath, Class paramClass) {
        this(paramClass);
        this.webPath = webPath;
    }

    public WebArgRef(WebMap webMap, Class paramClass) {
        this(paramClass);
        this.webMap = webMap;
    }
    
    public WebArgRef(WebEntity webEntity, Class paramClass) {
        this(paramClass);
        this.webEntity = webEntity;
    }
    
    public WebArgRef(WebState webState, Class paramClass) {
        this(paramClass);
        this.webState = webState;
    }    

    @SuppressWarnings("unchecked")
    public Object getValue(RequestContext rc) {
        Object value = null;
        if (paramClass == RequestContext.class) {
            value = rc;
        } else if (paramClass == HttpServletRequest.class) {
            value = rc.getReq();
        } else if (paramClass == HttpServletResponse.class) {
            value = rc.getRes();
        } else if (paramClass == ServletContext.class) {
            value = rc.getServletContext();
        } else if (webParam != null && paramClass == Map.class) {
            value = rc.getParamMap(webParam.value());
        } else if (webMap != null) {
            value = rc.getWebMap();
        } else if (webUser != null) {
            if (rc.getAuth() != null) {
                value = rc.getAuth().getUser();
            }
        } else if (webState != null){
            value = rc.getWebState(webState.value());
        }else if (webEntity != null){
            String paramName = webEntity.value();
            //FIXME: FOR NOW ONLY support LONG for WebEntity. THIS NEED TO BE FIXED!
            Long entityId = rc.getParam(paramName,Long.class);
            value = rc.getEntity(paramClass, entityId);
        } else if (webPath != null) {
            //if the index has been set, then, return the single Path and convert to the appropriate type.
            if (webPath.value() > -1) {
                value = rc.getCurrentPriPathAt(webPath.value(), paramClass, null);
            }
            //otherwise, return the full path
            else {
                value = rc.getCurrentPriFullPath();
            }
        } else {
            String paramName;
            if (webParam != null) {
                paramName = webParam.value();
            } else {
                paramName = paramClass.getSimpleName();
                //lowercase the first char
                paramName = paramName.substring(0, 1).toLowerCase() + paramName.substring(1);
            }

            value = rc.getParam(paramName, paramClass);
        }

        return value;
    }

}
