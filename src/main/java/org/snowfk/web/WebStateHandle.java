package org.snowfk.web;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snowfk.util.HttpRequestUtil;
import org.snowfk.util.ObjectUtil;

public abstract class WebStateHandle {


    /**
     * this is the default strategy that sets a cookie per parameter.
     */
    public static class MultiCookieWebStateHandle extends WebStateHandle {

        MultiCookieWebStateHandle(String uiContext, RequestContext rc) {
            super(uiContext, rc);
        }

        public String get(String paramName){
            return rc.getCookie(buildCookieName(paramName));
        }

        public void set(String paramName,Object value){
            rc.setCookie(buildCookieName(paramName), value == null ? null : value.toString());
        }

        private String buildCookieName(String paramName){
            StringBuilder sb = new StringBuilder(stateContext).append('.').append(paramName);
            return sb.toString();
        }
    }


    /**
     * this strategy shoves all parameters into a single cookie encoded as a url query string.
     */
    public static class SingleCookieWebStateHandle extends WebStateHandle {

        private Map<String,Object> parameterMap;

        SingleCookieWebStateHandle(String uiContext, RequestContext rc) {
            super(uiContext, rc);

            parameterMap = HttpRequestUtil.getMapFromQueryString(rc.getCookie(stateContext));
        }

        @Override
        public String get(String paramName) {
            Object value = parameterMap.get(paramName);
            return value == null ? null : value.toString();
        }

        @Override
        public void set(String paramName, Object value) {

            parameterMap.put(paramName, value);

            String params = HttpRequestUtil.getQueryStringFromMap(parameterMap);
            rc.setCookie(stateContext, StringUtils.isEmpty(params) ? null : params);
        }
    }




    public abstract String get(String paramName);

    public abstract void set(String paramName, Object value);


    protected RequestContext rc;
    protected String stateContext;
    
    WebStateHandle(String uiContext,RequestContext rc){
        this.rc = rc;
        this.stateContext = uiContext;
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
}
