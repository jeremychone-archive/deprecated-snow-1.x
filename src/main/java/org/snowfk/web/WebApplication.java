/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowfk.SnowRuntimeException;
import org.snowfk.util.FileUtil;
import org.snowfk.web.db.hibernate.HibernateHandler;
import org.snowfk.web.method.WebActionHandlerRef;
import org.snowfk.web.method.WebExceptionHandlerRef;
import org.snowfk.web.method.WebFileHandlerRef;
import org.snowfk.web.method.WebModelHandlerRef;
import org.snowfk.web.part.HttpPriResolver;
import org.snowfk.web.part.Part;
import org.snowfk.web.part.PartResolver;
import org.snowfk.web.part.PriUtil;
import org.snowfk.web.part.Part.Type;
import org.snowfk.web.renderer.FreemarkerRenderer;
import org.snowfk.web.renderer.JsonRenderer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;
import com.google.inject.name.Named;

@Singleton
public class WebApplication {
    static private Logger logger = LoggerFactory.getLogger(WebApplication.class);

    public enum Alert {
        NO_WEB_ACTION;
    }

    // set by WebApplicationLoader.load
    private String                      defaultModuleName;
    private String                      contentFolderPath;

    private FreemarkerRenderer          freemarkerRenderer;
    private JsonRenderer                jsonRenderer;
    private HibernateHandler            hibernateHandler;
    private Map<String, WebModule>      webModuleByName = new HashMap<String, WebModule>();

    private boolean                     initialized     = false;

    // _this is a quick hack for PartResolver. There might be a better way, but
    // not sure.
    // feel free to suggest.
    private WebApplication              _this           = this;
    private PartResolver                partResolver    = new PartResolver() {
                                                            public Part getPartFromPri(String pri) {
                                                                return _this.getPart(pri);
                                                            }
                                                        };

    private CurrentRequestContextHolder currentRequestContextHolder;

    @Inject
    public WebApplication(@Nullable HibernateHandler hibernateHandler) {
        this.hibernateHandler = hibernateHandler;
    }

    @Inject
    public void setCurrentRequestContextHolder(CurrentRequestContextHolder currentRequestContextHolder) {
        this.currentRequestContextHolder = currentRequestContextHolder;
    }

    /**
     * Can be injected via the application.properties, or could be set by the WebApplicationLoader from the
     * snow.applicationWebModuleConfigClass.getWebModuleName()
     * 
     * @param defaultModuleName
     */
    @Inject(optional = true)
    public void setSnowDefaultModuleName(@Nullable @Named("snow.defaultWebModuleName") String defaultModuleName) {
        this.defaultModuleName = defaultModuleName;
    }

    @Inject
    public void injectRenderers(FreemarkerRenderer freemarkerRenderer, JsonRenderer jsonRenderer) {
        this.freemarkerRenderer = freemarkerRenderer;
        this.jsonRenderer = jsonRenderer;
    }

    @Inject(optional = true)
    public void setContentFolderPath(@Nullable @Named("snow.contentFolderPath") String contentFolderPath) {
        this.contentFolderPath = contentFolderPath;
    }

    void addWebModule(WebModule webModule) {
        webModuleByName.put(webModule.getName(), webModule);
    }

    /* --------- WebApplication Life Cycle --------- */
    public synchronized void init() {
        if (!initialized) {

            /*--------- Initialize Hibernate ---------*/

            // init hibernate if needed
            if (hibernateHandler != null) {
                Object hibernateInterceptor = null;
                // look for the first HibernateInterceptor
                // FIXME: Need to support multi-interceptor. Right now, just support
                // one (take the first one)
                for (WebModule webModule : webModuleByName.values()) {
                    hibernateInterceptor = webModule.getHibernateInterceptor();
                    if (hibernateInterceptor != null) {
                        break;
                    }
                }
                for (WebModule webModule : webModuleByName.values()) {
                    hibernateHandler.addEntityClasses(webModule.getEntityClasses());
                }
                // Here we assume that the hibernateInterceptor is instanceof org.hibernate.Interceptor
                hibernateHandler.initSessionFactory((org.hibernate.Interceptor) hibernateInterceptor);
            }
            /*--------- /Initialize Hibernate ---------*/

            /*--------- Initialize WebModule ---------*/
            // init the web modules
            for (WebModule webModule : webModuleByName.values()) {
                try {
                    webModule.init();
                } catch (Exception e) {
                    logger.error("error initializing webapp", e);
                }
            }
            /*--------- /Initialize WebModule ---------*/

            /*--------- Initialize Freemarker ---------*/
            // NOTE: This has to be done after the webModule.init() since it
            // will need to get the TemplateModelProxy
            freemarkerRenderer.init();
            /*--------- /Initialize Freemarker ---------*/
            initialized = true;
        }

    }

    public void shutdown() {
        for (WebModule webModule : webModuleByName.values()) {
            try {
                webModule.shutdown();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /* --------- /WebApplication Life Cycle --------- */

    /*--------- Getters ---------*/
    public CurrentRequestContextHolder getCurrentRequestContextHolder() {
        return currentRequestContextHolder;
    }

    public Collection<WebModule> getWebModules() {
        return webModuleByName.values();
    }

    public WebModule getWebModule(String moduleName) {
        if (moduleName != null && !moduleName.equals("default")) {
            return webModuleByName.get(moduleName);
        } else {
            return getDefaultWebModule();
        }
    }

    public File getContentFolder() {
        if (contentFolderPath != null) {
            return new File(contentFolderPath);
        } else {
            return null;
        }
    }

    public PartResolver getPartResolver() {
        return partResolver;
    }

    /*--------- /Getters ---------*/

    /*--------- Part Factory Methods ---------*/
    public Part getPart(String pagePri) {
        return getPart(pagePri, null);
    }

    /**
     * TODO: Must support the config: Type file override. First look at the content folder for config.., then, in the
     * base folder.
     * 
     * @param pagePri
     * @param framePri
     * @return
     */
    public Part getPart(String pagePri, String framePri) {
        /*--------- Get the pri info ---------*/
        Type partType = PriUtil.getPartType(pagePri);

        // by default, the partType is Template ("t")
        if (partType == null) {
            partType = Type.t;
        }
        /*
         * if (partType == null) { throw new RuntimeException("Wrong pri format, no part type: " + pagePri); }
         */

        WebModule webModule = getWebModule(PriUtil.getModuleNameFromPri(pagePri));

        // use the default module
        // TODO: problably need to take the currentModule
        if (webModule == null) {
            webModule = getDefaultWebModule();
        }
        /*
         * now, use the default module if (webModule == null) { throw new
         * RuntimeException("Wrong pri format, unknown module name: " + pagePri); }
         */
        /*--------- /Get the pri info ---------*/

        /*--------- Compute the folder and filePath ---------*/
        // base folder for the part

        File partBaseFolder = null;
        switch (partType) {

            case t:
                partBaseFolder = webModule.getViewFolder();
                break;
            case c:

                File contentFolder = getContentFolder();
                if (contentFolder != null) {
                    partBaseFolder = new File(contentFolder, partType.prefix() + File.separatorChar
                                            + webModule.getName());
                } else {
                    throw new RuntimeException("Cannot support content part, contentFolderPath not defined (please defined it in the application.properties): " + pagePri);
                }
                break;
            case config:
                partBaseFolder = webModule.getConfigFolder();
                break;
        }

        String relativePartFilePath;
        if (framePri != null) {
            relativePartFilePath = PriUtil.getRelativePartFilePath(framePri);
        } else {
            relativePartFilePath = PriUtil.getRelativePartFilePath(pagePri);

            // if it is a content part and it has a '[]' in the path, then,
            // explode the file path
            // FIXME: We need to use "|" rather than '[' ']'
            if (partType == Part.Type.c && relativePartFilePath.indexOf('[') != -1) {
                relativePartFilePath = FileUtil.splitIdFolder2(relativePartFilePath, '/');
            }

            String[] fileNameAndExt = FileUtil.getFileNameAndExtension(relativePartFilePath);

            String newFileName = webModule.getPathWoExtFromPriWoExt(fileNameAndExt[0]);

            if (newFileName != fileNameAndExt[0]) {
                relativePartFilePath = new StringBuilder(newFileName).append(fileNameAndExt[1]).toString();
            }

        }
        /*--------- /Compute the folder and filePath---------*/

        /*--------- Build the Part ---------*/

        // check if it is a .json part, if yes, remove the .json from the
        // extension
        boolean isJsonPart = false;
        String[] pagePriNameAndExt = FileUtil.getFileNameAndExtension(pagePri);
        if (".json".equals(pagePriNameAndExt[1])) {
            pagePri = pagePriNameAndExt[0];
            isJsonPart = true;
        }

        Part part = new Part(pagePri, framePri, webModule, partType);

        if (isJsonPart) {
            part.setFormatType(Part.FormatType.json);
        } else {
            File resourceFile = new File(partBaseFolder, relativePartFilePath);
            part.setResourcePath(resourceFile.getAbsolutePath());
        }
        /*--------- /Build the Part ---------*/

        return part;
    }

    /*--------- /Part Factory Methods ---------*/

    /*--------- Part Processing ---------*/

    public void processJsonPart(Part part, RequestContext rc) throws Throwable {
        Object data = null;

        String pathInfo = rc.getPathInfo();
        WebModule module = part.getWebModule();
        rc.pushCurrentWebModule(module);

        // if it is the _actionResponse.json, the, just get the data from
        // WebActionResponse
        if (pathInfo.equals(HttpPriResolver.PRI_PATH_ACTION_RESPONSE_JSON)) {
            data = rc.getWebActionResponse();
        }
        // otherwise, process the webModel
        else {

            Map m = new HashMap();

            // process the part
            Part oldPart = rc.getCurrentPart();
            rc.setCurrentPart(part);
            proccessWebModels(module.getName(), m, rc);
            rc.setCurrentPart(oldPart);

            // first try to get the _jsonData
            data = m.get("_jsonData");
            // if no _jsonData is not set, then, take the model as the data
            if (data == null) {
                data = m;
            }

        }
        jsonRenderer.processPart(part, data, rc.getWriter());
        rc.pollCurrentWebModule();
    }

    @SuppressWarnings("unchecked")
    public void processFreemarkerPart(Part part, RequestContext rc) throws Throwable {

        // build the new model
        Map rootModel = rc.getRootModel();

        Part oldPart = rc.getCurrentPart();

        rc.setCurrentPart(part);
        WebModule module = part.getWebModule();
        rc.pushCurrentWebModule(module);

        /*--------- Build the model ---------*/
        // if if the model for this module has already been built
        Map m = rc.getWebMap();

        proccessWebModels(module.getName(), m, rc);

        freemarkerRenderer.processPart(part, rootModel, rc.getWriter());

        rc.setCurrentPart(oldPart);
        rc.pollCurrentWebModule();
    }

    public boolean processWebFilePart(Part part, RequestContext rc) throws Exception {
        WebModule module = part.getWebModule();
        rc.pushCurrentWebModule(module);
        String priPath = rc.getCurrentPriFullPath();
        WebFileHandlerRef webFileRef = module.getWebFileRef(priPath);
        if (webFileRef != null) {
            webFileRef.invokeWebFile(rc);
            rc.pollCurrentWebModule();
            return true;
        } else {
            rc.pollCurrentWebModule();
            return false;
        }

    }

    /*--------- /Part Processing ---------*/

    /*--------- WebMethod Processing ---------*/
    public WebActionResponse processWebAction(String air, RequestContext rc) throws Throwable {
        String moduleName, actionName;

        // This is to support legacy module name way (as of now 2011-09-03 multiple module is deprecated)
        String[] webModuleNameAndActionName = air.split(":");
        if (webModuleNameAndActionName.length > 1) {
            moduleName = webModuleNameAndActionName[0];
            actionName = webModuleNameAndActionName[1];
        } else {
            // New way, single Module
            moduleName = getDefaultWebModuleName();
            actionName = webModuleNameAndActionName[0];
        }
        return processWebAction(moduleName, actionName, rc);
    }

    public WebActionResponse processWebAction(String webModuleName, String webActionName, RequestContext rc)
                            throws Throwable {
        WebModule webModule = null;

        if (webModuleName != null) {
            webModule = getWebModule(webModuleName);
        } else {
            webModule = getDefaultWebModule();
        }

        rc.pushCurrentWebModule(webModule);
        if (webModule == null) {
            throw new Exception("No webModule found for: " + webModuleName);
        }

        WebActionHandlerRef webActionRef = webModule.getWebActionRef(webActionName);
        if (webActionRef == null) {
            throw new SnowRuntimeException(Alert.NO_WEB_ACTION, "WebAction", webModuleName + ":" + webActionName);
        }

        // --------- Invoke Method --------- //
        WebHandlerInterceptor methodInterceptor = webModule.getWebHandlerMethodInterceptor();
        boolean invokeWebAction = true;

        Object result = null;

        try {
            if (methodInterceptor != null) {
                invokeWebAction = methodInterceptor.before(webActionRef.getMethod(), rc);
            }

            if (invokeWebAction) {
                result = webActionRef.invokeWebAction(rc);
            }

            if (methodInterceptor != null) {
                methodInterceptor.after(webActionRef.getMethod(), rc);
            }
        } catch (Throwable t) {
            processWebExceptionHandler(webModule, t, rc);
        }
        // --------- /Invoke Method --------- //

        WebActionResponse response = new WebActionResponse(result);
        rc.pollCurrentWebModule();
        return response;
    }

    @SuppressWarnings("unchecked")
    public void proccessWebModels(String webModuleName, Map m, RequestContext rc) throws Throwable {
        WebModule webModule = getWebModule(webModuleName);
        if (webModule == null) {
            throw new Exception("No webModule found for: " + webModuleName);
        }

        // get the rootModelBuilder
        WebModelHandlerRef rootWmr = webModule.getWebModelRef("/");
        if (rootWmr != null) {
            invokeWebModelRef(webModule, rootWmr, m, rc);
        }

        // Match and process the "startsWith" webModels
        String[] priPaths = rc.getCurrentPriPaths();
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < priPaths.length; i++) {
            String path = pathBuilder.append('/').append(priPaths[i]).toString();
            WebModelHandlerRef webModelRef = webModule.getWebModelRef(path);
            invokeWebModelRef(webModule, webModelRef, m, rc);
        }

        // Match and process the "matches" webModels
        List<WebModelHandlerRef> matchWebModelRefs = webModule.getMatchWebModelRef(pathBuilder.toString());
        for (WebModelHandlerRef webModelRef : matchWebModelRefs) {
            invokeWebModelRef(webModule, webModelRef, m, rc);
        }
    }

    private void invokeWebModelRef(WebModule webModule, WebModelHandlerRef webModelRef, Map m, RequestContext rc)
                            throws Throwable {

        if (webModelRef != null) {

            WebHandlerInterceptor methodInterceptor = webModule.getWebHandlerMethodInterceptor();
            boolean invokeWebAction = true;

            try {
                if (methodInterceptor != null) {
                    invokeWebAction = methodInterceptor.before(webModelRef.getMethod(), rc);
                }

                if (invokeWebAction) {
                    webModelRef.invokeWebModel(m, rc);
                }

                if (methodInterceptor != null) {
                    methodInterceptor.after(webModelRef.getMethod(), rc);
                }
            } catch (Throwable e) {
                processWebExceptionHandler(webModule, e, rc);
            }

        }
    }

    /**
     * This will look for a matching WebExceptionHandlerRef and invoke it, otherwise, will throw the cause of the
     * InvocationTargetException
     * 
     * @param webModule
     * @param e
     * @param rc
     * @throws Throwable
     *             Will throw the cause of the InvocationTargetException if no Throwable t = e.getCause(); found
     */
    private void processWebExceptionHandler(WebModule webModule, Throwable e, RequestContext rc) throws Throwable {
        Throwable t = null;

        if (e instanceof InvocationTargetException) {
            t = ((InvocationTargetException) e).getCause();
        }

        if (t != null) {
            WebExceptionHandlerRef ref = webModule.getWebExceptionRef(t.getClass());

            // if we find the issue
            if (ref != null) {
                ref.invokeWebExceptionHandler(t, rc);
                // TODO: miwht want to try catch, and throw the cause as well
                // (to be consistent
            } else {
                throw t;
            }
        } else {
            throw e;
        }
    }

    /*--------- /WebMethod Processing ---------*/
    public WebModule getDefaultWebModule() {
        return webModuleByName.get(getDefaultWebModuleName());
    }

    public String getDefaultWebModuleName() {
        return defaultModuleName;
    }

}
