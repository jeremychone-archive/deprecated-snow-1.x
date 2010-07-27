package org.snowfk.web;

import org.snowfk.util.ObjectUtil;

public class WebStateHandle {

    private RequestContext rc;
    private String stateContext;
    
    WebStateHandle(String uiContext,RequestContext rc){
        this.rc = rc;
        this.stateContext = uiContext;
    }
    

    public String get(String paramName){
        
        String value = rc.getCookie(buildCookieName(paramName));
        return value;
    }
    
    
    public <T> T get(String paramName,Class<T> tClass){
        String value = get(paramName);
        T tValue = ObjectUtil.getValue(value, tClass, null);
        return tValue;
    }
    
    public <T> T get(String paramName,Class<T> tClass,T defaulValue){
        String value = get(paramName);
        T tValue = ObjectUtil.getValue(value, tClass, defaulValue);
        return tValue;
    }
    
    
    public void set(String paramName,Object value){
        if (value == null){
            rc.setCookie(buildCookieName(paramName), null);
        }else{
            rc.setCookie(buildCookieName(paramName), value.toString());
        }
    }
    
    
    private String buildCookieName(String paramName){
        StringBuilder sb = new StringBuilder(stateContext).append('.').append(paramName);
        return sb.toString();
    }
    
}
