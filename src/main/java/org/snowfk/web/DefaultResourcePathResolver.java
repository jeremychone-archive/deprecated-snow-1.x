package org.snowfk.web;

public class DefaultResourcePathResolver implements ResourcePathResolver {

    @Override
    public String resolve(RequestContext rc) {
        return rc.getPathInfo();
    }

}
