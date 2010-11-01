package org.snowfk.web.renderer.freemarker;

import java.io.IOException;

import org.snowfk.web.RequestContext;
import org.snowfk.web.WebApplication;
import org.snowfk.web.part.Part;

import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.TemplateException;

public class IncludeTemplateBase {
	protected WebApplication webApplication;
	
    @Inject
    public void setTemplateDirective(WebApplication webApplication){
        this.webApplication = webApplication;
    }
    
    protected void includeTemplate(RequestContext rc, String templateNameOrPri,Environment env) throws IOException, TemplateException{
        String moduleName = rc.getCurrentPart().getWebModule().getName();
        Part part = null;
        if (templateNameOrPri != null){
            //if it is an "/" or "*" template name, then, just asked to auto resolve it (by giving an empty path to the PRI
            if (templateNameOrPri.length() == 1 && ("*".equals(templateNameOrPri) || "/".equals(templateNameOrPri))){
                throw new RuntimeException("Does not support include empty template name");
            }
            //if there is a ":" then assume it is a valid pri
            else if (templateNameOrPri.indexOf(':') != -1 ){
                part = webApplication.getPart(templateNameOrPri);
            }
            else{
                part = webApplication.getPart("t:" + moduleName + ":" + templateNameOrPri);
            } 
            
            String fpTemplateName = FreemarkerUtil.getTemplateNameFromPart(part);
            
            env.include(fpTemplateName, "UTF-8", true);
        }    	
    }
}
