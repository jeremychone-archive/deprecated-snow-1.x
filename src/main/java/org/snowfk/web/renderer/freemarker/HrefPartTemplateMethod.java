/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;


import java.util.List;

import org.snowfk.util.FileUtil;
import org.snowfk.web.*;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getParam;

@Singleton
public class HrefPartTemplateMethod implements TemplateMethodModelEx {

    @Inject
    Application webApplication;
    @Inject
    CurrentRequestContextHolder currentRCHolder;

    @Override
    public Object exec(List args) throws TemplateModelException {

        String path = getParam(args.get(0),String.class);
        
        RequestContext rc = currentRCHolder.getCurrentRequestContext();
        
        String contextPath = rc.getContextPath();
        
        path = FileUtil.encodeFileName(path);
        
        String href = new StringBuilder(contextPath).append(path).toString();
        return href;
    }

}
