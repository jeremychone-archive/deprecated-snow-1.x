/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.method;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snowfk.web.RequestContext;



public class WebModelRef extends WebMethodRef implements PathMatcher {

    WebModel webModel;

    public WebModelRef(Object object, Method method, WebModel webModel) {
        super(object, method);
        this.webModel = webModel;
        
        initWebParamRefs();
    }

    
    /* (non-Javadoc)
     * @see org.snowfk.web.method.PathMatcher#matchesPath(java.lang.String)
     */
    @Override
    public boolean matchesPath(String path){
        if (webModel != null && webModel.matches().length > 0){
            /**/
            for (String regex : webModel.matches()){
                Pattern pat = Pattern.compile(regex);
                Matcher mat = pat.matcher(path);
                Boolean matches = mat.matches();
                if (matches){
                    return true;
                }
                
            }
            return false;
        }else{
            return false;
        }
    }
    
    public void invokeWebModel(Map m,RequestContext rc) throws Exception{
        Object[] paramValues = new Object[webArgRefs.size()];
        
        //the first param of a WebModel MUST be the Model Map
        paramValues[0] = m;
        
        if (webArgRefs.size() > 1){
            for (int i = 1; i < paramValues.length;i++){
                WebArgRef webParamRef = webArgRefs.get(i);
                paramValues[i] = webParamRef.getValue(rc);
            }
        }
        
        method.invoke(webHandler,paramValues);
    }
    
    public String toString(){
        return "WebModelRef: " + method.getName();
    }
}
