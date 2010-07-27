/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings("serial")
public class SnowServlet extends HttpServlet {

    private WebController webController;
    private Logger logger = LoggerFactory.getLogger(SnowServlet.class);
    
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();

        try {
            File webInfFolder = new File(servletContext.getRealPath("WEB-INF/"));
            File snowFolder = new File(webInfFolder, "snow");
            
            
            WebApplicationLoader appLoader = new WebApplicationLoader(snowFolder, servletContext);
            String appName = appLoader.getWebAppFolder().getName();
            System.out.println("==== Snow Application : " + appName + " ===========");
            appLoader.load();
            System.out.println("loading... done");
            
            webController = appLoader.getWebController();
            webController.init();
            System.out.println("init... done");
            System.out.println("==== /Snow Application : " + appName + " ===========");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        }
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            webController.service(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    public void destroy() {

        webController.destroy();

        super.destroy();

    }
}
