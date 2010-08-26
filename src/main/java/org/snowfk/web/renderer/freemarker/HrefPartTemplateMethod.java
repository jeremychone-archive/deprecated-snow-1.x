/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;


import java.util.List;

import org.snowfk.web.*;
import org.snowfk.web.names.ServletContextPath;
import org.snowfk.web.part.Part;
import org.snowfk.web.part.HttpPriResolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getParam;

@Singleton
public class HrefPartTemplateMethod implements TemplateMethodModelEx {

    WebApplication webApplication;
    String contextPath;

    @Inject
    public HrefPartTemplateMethod(WebApplication webApplication,@Nullable @ServletContextPath ContextPathFinder contextPathFinder){
        this.webApplication = webApplication;
        this.contextPath = contextPathFinder.getContextPath();
    }

    
    @Override
    public Object exec(List args) throws TemplateModelException {

        String pri = getParam(args.get(0),String.class);
        
        Part part = webApplication.getPart(pri);
        
        String hrefPart =  HttpPriResolver.getHrefForPart(part);
        hrefPart = new StringBuilder(contextPath).append(hrefPart).toString();
        return hrefPart;
    }

}
