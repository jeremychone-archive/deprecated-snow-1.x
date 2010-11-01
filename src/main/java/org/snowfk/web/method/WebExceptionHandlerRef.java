package org.snowfk.web.method;

import java.lang.reflect.Method;

import org.snowfk.web.RequestContext;
import org.snowfk.web.method.argument.WebArgRef;

public class WebExceptionHandlerRef extends BaseWebHandlerRef {
	@SuppressWarnings("unused")
	private WebExceptionHandler webExceptionHandler;
	
	private Class<? extends Throwable> exceptionClass;
	
	public WebExceptionHandlerRef(Object webHandler, Method method,WebExceptionHandler webExceptionHandler) {
		super(webHandler, method);
		
		this.webExceptionHandler = webExceptionHandler;
		
		initWebParamRefs();
		
		//for now, 
		exceptionClass = webArgRefs.get(0).getArgClass();
		
	}
	
	public Class getThrowableClass(){
		return exceptionClass;
	}
	
    public void invokeWebExceptionHandler(Throwable e,RequestContext rc) throws Exception{
        Object[] paramValues = new Object[webArgRefs.size()];
        
        //the first param of a WebModel MUST be the Model Map
        paramValues[0] = e;
        
        if (webArgRefs.size() > 1){
            for (int i = 1; i < paramValues.length;i++){
                WebArgRef webParamRef = webArgRefs.get(i);
                paramValues[i] = webParamRef.getValue(rc);
            }
        }
        
        method.invoke(webHandler,paramValues);
    }	

}
