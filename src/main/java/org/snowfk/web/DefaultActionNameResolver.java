package org.snowfk.web;

public class DefaultActionNameResolver implements ActionNameResolver {

    @Override
    public String resolve(RequestContext rc) {
        return rc.getParam("action");
    }

}
