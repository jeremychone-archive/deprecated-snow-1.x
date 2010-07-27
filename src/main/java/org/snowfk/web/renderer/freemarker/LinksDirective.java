/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;



import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snowfk.web.RequestContext;
import org.snowfk.web.WebApplication;
import org.snowfk.web.part.Part;
import org.snowfk.web.part.HttpPriResolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;



import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Singleton
public class LinksDirective implements TemplateDirectiveModel {
    static final private String DEBUG_LINK_STRING = "_debug_links";
    private enum LinkItemType {
        css, js
    };

    private PartCacheManager partCacheManager;
    private WebApplication webApplication;
    
    @Inject
    public LinksDirective(PartCacheManager partCacheManager,WebApplication webApplication){
        this.partCacheManager = partCacheManager;
        this.webApplication = webApplication;
    }
    
    @Override
    public void execute(Environment env, Map param, TemplateModel[] tms, TemplateDirectiveBody body)
                            throws TemplateException, IOException {

        RequestContext rc = FreemarkerUtil.getDataModel("r.rc", RequestContext.class);
        
        Boolean debug_links = rc.getParam(DEBUG_LINK_STRING, Boolean.class, null);
        //if not null, make sure we set the cookie with the value
        if (debug_links != null){
            rc.setCookie(DEBUG_LINK_STRING, debug_links);
        }
        //if there is not debug_link param in the URL, check the cookie (set false if not found)
        else{
            debug_links = rc.getCookie(DEBUG_LINK_STRING, Boolean.class, false);
        }
        
        // build the list for pris
        StringWriter sw = new StringWriter();
        body.render(sw);
        String bodyStr = sw.toString();
        String[] prisStr = bodyStr.split("(\\r| |\\n)");

        // build the list of pris
        List<String> pris = new ArrayList<String>();
        for (String jsPri : prisStr) {
            String pri = jsPri.trim();
            if (pri.length() > 0) {
                pris.add(pri);
            }
        }

        // get the ext
        LinkItemType itemType = LinkItemType.css;
        if (pris.get(0).endsWith(".js")) {
            itemType = LinkItemType.js;
        }

        BufferedWriter bw = new BufferedWriter(env.getOut());
        if (debug_links) {
            for (String pri : pris) {
                Part part = webApplication.getPart(pri);
                
                String href = new StringBuilder(rc.getReq().getContextPath()).append(HttpPriResolver.getHrefForPart(part)).toString();
                
                switch (itemType) {
                    case css:
                        bw.write("<link type='text/css' href='");
                        bw.write(href);
                        bw.write("'  rel='stylesheet'  />\n");
                        break;
                    case js:
                        bw.write("<script type='text/javascript' src='");
                        bw.write(href);
                        bw.write("'></script>\n");
                        break;
                }
            }
        } else {

            // build the contextPath
            String contextPath = "";
            if (param.get("contextPath") != null) {
                contextPath = param.get("contextPath").toString();
                if (contextPath.indexOf(":") != -1){
                    Part contextPathPart = webApplication.getPart(contextPath);
                    String newContextPath = new StringBuilder(rc.getReq().getContextPath()).append(HttpPriResolver.getHrefForPart(contextPathPart)).toString();
                    //System.out.println("LinksDirective2: ContextPath: " + newContextPath );
                    contextPath = newContextPath;
                }
            }

            // get the href
            String href = partCacheManager.getHrefForPartPris(pris, contextPath);

            switch (itemType) {
                case css:
                    bw.write("<link type='text/css' href='");
                    bw.write(href);
                    bw.write("'  rel='stylesheet'  />\n");
                    break;
                case js:
                    bw.write("<script type='text/javascript' src='");
                    bw.write(href);
                    bw.write("'></script>\n");
                    break;
            }

            
        }
        bw.flush();
    }



}
