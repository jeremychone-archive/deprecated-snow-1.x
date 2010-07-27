/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;

import java.io.IOException;
import java.util.Map;

import org.snowfk.web.RequestContext;
import org.snowfk.web.WebApplication;
import org.snowfk.web.part.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;


import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getParam;
import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getDataModel;


import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Singleton
public class IncludeTemplateDirective implements TemplateDirectiveModel {

    private WebApplication webApplication;
    
    @Inject
    public void setTemplateDirective(WebApplication webApplication){
        this.webApplication = webApplication;
    }
    
    @Override
    public void execute(Environment env, Map args, TemplateModel[] arg2, TemplateDirectiveBody arg3)
                            throws TemplateException, IOException {

        String templateName = getParam(args,"name",String.class); 
        RequestContext rc = getDataModel("r.rc", RequestContext.class);
        
        String moduleName = rc.getCurrentPart().getWebModule().getName();
        Part part = null;
        if (templateName != null){
            //if it is an "/" or "*" template name, then, just asked to auto resolve it (by giving an empty path to the PRI
            if (templateName.length() == 1 && ("*".equals(templateName) || "/".equals(templateName))){
                throw new TemplateException("does not support include empty template name anymore",env);
            }
            //if there is a ":" then assume it is a valid pri
            else if (templateName.indexOf(':') != -1 ){
                part = webApplication.getPart(templateName);
            }
            else{
                part = webApplication.getPart("t:" + moduleName + ":" + templateName);
            } 
            
            String fpTemplateName = FreemarkerUtil.getTemplateNameFromPart(part);
            
            env.include(fpTemplateName, "UTF-8", true);
        }
        

    }

}
