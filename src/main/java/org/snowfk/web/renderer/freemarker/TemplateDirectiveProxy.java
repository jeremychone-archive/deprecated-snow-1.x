/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;

import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getDataModel;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowfk.web.RequestContext;
import org.snowfk.web.method.WebTemplateDirectiveHandlerRef;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class TemplateDirectiveProxy implements TemplateDirectiveModel {
    static private Logger logger = LoggerFactory.getLogger(TemplateDirectiveProxy.class);
    
    private String name;
    private WebTemplateDirectiveHandlerRef webTemplateDirectiveRef;
    
    public TemplateDirectiveProxy(String name,WebTemplateDirectiveHandlerRef webTemplateDirectiveRef){
        this.name = name;
        this.webTemplateDirectiveRef = webTemplateDirectiveRef;
    }

    public String getName(){
        return name;
    }
    
    public void execute(Environment env, Map paramMap, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException,
                            IOException {
        RequestContext rc = getDataModel("r.rc", RequestContext.class);
        try {
            webTemplateDirectiveRef.invokeWebTemplateDirective(env, paramMap, rc);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
