package org.snowfk.web;

import javax.servlet.http.*;

public class ServletRequestContextPathFinder implements ContextPathFinder {

    private WebController webController;

    public ServletRequestContextPathFinder(WebController webController) {
        this.webController = webController;
    }

    @Override
    public String getContextPath() {

        CurrentRequestContextHolder holder = webController.getCurrentRequestContextHolder();
        if(holder != null) {
            RequestContext requestContext = holder.getCurrentRequestContext();
            if(requestContext != null) {
                HttpServletRequest request = requestContext.getReq();
                if(request != null) {
                    return request.getContextPath();
                }
            }
        }

        return null;
    }
}
