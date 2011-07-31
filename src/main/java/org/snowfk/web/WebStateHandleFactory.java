package org.snowfk.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class WebStateHandleFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebStateHandleFactory.class);


    public enum WebStateMode {
        SingleCookie, MultiCookie
    }


    private WebStateMode webStateMode = WebStateMode.MultiCookie;


    public WebStateMode getWebStateMode() {
        return webStateMode;
    }

    @Inject(optional = true)
    public void setWebStateMode(@Named("snow.webStateMode") String webStateModeStr) {
        try {
            webStateMode = WebStateMode.valueOf(webStateModeStr);
        }
        catch(IllegalArgumentException e) {
            logger.warn("invalid WebStateMode property : " + webStateModeStr + ".  Will default to MultiCookie.");
        }
    }

    public WebStateHandle constructWebStateHandle(String uiContext, RequestContext rc) {
        switch(webStateMode) {
            case SingleCookie:
                return new WebStateHandle.SingleCookieWebStateHandle(uiContext, rc);

            case MultiCookie:
            default:
                return new WebStateHandle.MultiCookieWebStateHandle(uiContext, rc);
        }
    }
}
