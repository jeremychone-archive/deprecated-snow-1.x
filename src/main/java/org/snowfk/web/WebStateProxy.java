package org.snowfk.web;

public class WebStateProxy{
    private RequestContext rc;
    
    public WebStateProxy(RequestContext rc){
        this.rc = rc;
    }
    
    public WebStateHandle get(String stateContext){
        return rc.getWebState(stateContext);
    }
}
