/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snowfk.annotation.Nullable;
import org.snowfk.web.auth.AuthService;
import org.snowfk.web.db.hibernate.HibernateDaoHelper;
import org.snowfk.web.db.hibernate.HibernateInterceptorBinding;
import org.snowfk.web.method.WebActionHandler;
import org.snowfk.web.method.WebActionHandlerRef;
import org.snowfk.web.method.WebExceptionHandler;
import org.snowfk.web.method.WebExceptionHandlerRef;
import org.snowfk.web.method.WebFileHandler;
import org.snowfk.web.method.WebFileHandlerRef;
import org.snowfk.web.method.WebModelHandler;
import org.snowfk.web.method.WebModelHandlerRef;
import org.snowfk.web.method.WebTemplateDirectiveHandler;
import org.snowfk.web.method.WebTemplateDirectiveHandlerRef;
import org.snowfk.web.method.argument.WebParameterParser;
import org.snowfk.web.names.EntityClasses;
import org.snowfk.web.names.LeafPaths;
import org.snowfk.web.names.WebHandlers;
import org.snowfk.web.part.CustomFramePriPath;
import org.snowfk.web.part.Part;
import org.snowfk.web.renderer.freemarker.TemplateDirectiveProxy;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class WebModule {

	// // Set by WebApplicationLoader
	private String name;
	private List<Object> webHandlers;
	private File folder;
	private File viewFolder;
	private File configFolder;

	// // Injected from WebModuleConfig
	private Class[] entityClasses;
	// // Set Initialization Methods
	private Map<String, WebModelHandlerRef> webModelByStartsWithMap = new HashMap<String, WebModelHandlerRef>();
	private List<WebModelHandlerRef> webModelRefList = new ArrayList<WebModelHandlerRef>();
	private Map<String, WebActionHandlerRef> webActionDic = new HashMap<String, WebActionHandlerRef>();
	private List<WebFileHandlerRef> webFileList = new ArrayList<WebFileHandlerRef>();
	private Map<Class<? extends Throwable>,WebExceptionHandlerRef> webExceptionHanderMap = new HashMap<Class<? extends Throwable>, WebExceptionHandlerRef>();
    private Map<Class<? extends Annotation>,WebParameterParser> webParameterParserMap = new HashMap<Class<? extends Annotation>, WebParameterParser>();

	private List<TemplateDirectiveProxy> templateDirectiveProxyList = new ArrayList<TemplateDirectiveProxy>();

	// injected (optional)
	private String[] leafPaths;

	// injected (optional)
	private AuthService authService;

	// injected (optional) (use @HibernateInterceptorBinding to bind)
	private Object hibernateInterceptor;

	// injection (optional)
	private CustomFramePriPath customeFramePriPath;

	// injection (optional)
	private WebModuleLifeCycle webModuleLifecyle;

	// injection (optional)
	private RequestLifeCycle requestLifeCycle;
	
	// injection (optional)
	private WebHandlerInterceptor webHandlerMethodInterceptor;

    // injection (optional)
    private WebParameterParser[] webParameterParsers;

	// injection (optional)
	private HibernateDaoHelper hibernateDaoHelper;

    // injection (required!)
    private WebStateHandleFactory webStateHandleFactory;

	// Injected
	private Injector injector;

	public WebModule() {

	}

	/* --------- Injector Methods --------- */
	public void injectMembers(Object obj) {
		injector.injectMembers(obj);
	}

	/* --------- /Injector Methods --------- */

	/*--------- Getters ---------*/
	WebActionHandlerRef getWebActionRef(String actionName) {
		return webActionDic.get(actionName);
	}

	WebModelHandlerRef getWebModelRef(String path) {
		return webModelByStartsWithMap.get(path);
	}

	List<WebModelHandlerRef> getMatchWebModelRef(String fullPriPath) {
		List<WebModelHandlerRef> matchWebModelRefs = new ArrayList<WebModelHandlerRef>();

		for (WebModelHandlerRef webModelRef : webModelRefList) {
			// System.out.println("WebModule.getMatchWebModeulRef: " +
			// webModelRef.toString());
			boolean match = webModelRef.matchesPath(fullPriPath);
			if (match) {
				matchWebModelRefs.add(webModelRef);
			}
		}

		return matchWebModelRefs;
	}

	WebFileHandlerRef getWebFileRef(String path) {
		for (WebFileHandlerRef webFileRef : webFileList) {
			boolean match = webFileRef.matchesPath(path);
			if (match) {
				return webFileRef;
			}
		}
		return null;
	}
	
	
	WebExceptionHandlerRef getWebExceptionRef(Class<? extends Throwable> exceptionClass){
		WebExceptionHandlerRef ref = null;
		
		do {
			ref = webExceptionHanderMap.get(exceptionClass);
			if (ref != null){
				return ref;
			}
			Class clzz = exceptionClass.getSuperclass();
			
			//TODO: we need to test this one further
			if (Throwable.class.isAssignableFrom(clzz)){
				exceptionClass = (Class<? extends Throwable>)clzz;
			}else{
				return null;
			}
			 
			
		}while (exceptionClass != null);
		
		return ref;
	}

	public List<TemplateDirectiveProxy> getTemplateDirectiveProxyList() {
		return templateDirectiveProxyList;
	}

	// --------- Folders --------- //
	public File getViewFolder() {
		return viewFolder;
	}

	public File getConfigFolder() {
		return configFolder;
	}

	// --------- /Folders --------- //

	public String getPathWoExtFromPriWoExt(String priWoExt) {
		if (leafPaths == null || leafPaths.length < 1) {
			return priWoExt;
		} else {
			for (String leafTemplatePath : leafPaths) {
				if (priWoExt.startsWith(leafTemplatePath)) {
					// remove the eventual '/'
					if (leafTemplatePath.endsWith("/")) {
						leafTemplatePath = leafTemplatePath.substring(0, leafTemplatePath.length() - 1);
					}
					return leafTemplatePath;
				}
			}
			// if the priWoExt does match any template, return the priWoExt
			return priWoExt;
		}
	}

	//Deprecated big time
	public File getModuleDbXmlFile() {
		return new File(folder, "/config/db.xml");
	}
	
	

	/*--------- /Getters ---------*/

	/*--------- Dependency Methods ---------*/

	@Inject(optional = true)
	public void injectHiberndateDaoHelper(HibernateDaoHelper hibernateDaoHelper) {
		this.hibernateDaoHelper = hibernateDaoHelper;
	}

	public HibernateDaoHelper getHibernateDaoHelper() {
		return hibernateDaoHelper;
	}

	public Class[] getEntityClasses() {
		return this.entityClasses;
	}

	@Inject
	public void setEntityClasses(@EntityClasses Class[] entityClasses) {
		this.entityClasses = entityClasses;
	}

	public String[] getLeafPaths() {
		return leafPaths;
	}

	@Inject(optional = true)
	public void setLeafPaths(@LeafPaths String[] leafPaths) {
		this.leafPaths = leafPaths;
	}

	public Object getHibernateInterceptor() {
		return hibernateInterceptor;
	}

	@Inject(optional = true)
	public void setHibernateInterceptor(@HibernateInterceptorBinding Object hibernateInterceptor) {
		this.hibernateInterceptor = hibernateInterceptor;
	}

	public CustomFramePriPath getCustomeFramePriPath() {
		return customeFramePriPath;
	}

	@Inject(optional = true)
	public void setCustomeFramePriPath(CustomFramePriPath customeFramePriPath) {
		this.customeFramePriPath = customeFramePriPath;
	}

	@Inject(optional = true)
	public void setWebModuleLifecyle(WebModuleLifeCycle webModuleLifecyle) {
		this.webModuleLifecyle = webModuleLifecyle;
	}

	public RequestLifeCycle getRequestLifeCycle() {
		return requestLifeCycle;
	}

	@Inject(optional = true)
	public void setRequestLifeCyle(RequestLifeCycle rlc) {
		requestLifeCycle = rlc;
	}
	
	public WebHandlerInterceptor getWebHandlerMethodInterceptor(){
		return webHandlerMethodInterceptor;
	}
	
	@Inject(optional = true)
	public void setWebHandlerMethodInterceptor(WebHandlerInterceptor webHandlerMethodInterceptor){
		this.webHandlerMethodInterceptor = webHandlerMethodInterceptor;
	}

    public WebParameterParser[] getWebParameterParsers() {
        return webParameterParsers;
    }

    @Inject(optional = true)
    public void setWebParameterParsers(WebParameterParser[] webParameterParsers) {
        this.webParameterParsers = webParameterParsers;
    }


	public AuthService getAuthService() {
		return authService;
	}

	@Inject(optional = true)
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	public List<Object> getWebHandlers(){
		return webHandlers;
	}
	@Inject
	public void setWebHandlers(@Nullable @WebHandlers List<Object> webHandlers) {
		this.webHandlers = webHandlers;
	}

	public String getName() {
		return name;
	}

	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

    public WebStateHandleFactory getWebStateHandleFactory() {
        return webStateHandleFactory;
    }

    @Inject
    public void setWebStateHandleFactory(WebStateHandleFactory webStateHandleFactory) {
        this.webStateHandleFactory = webStateHandleFactory;
    }

    /*--------- /Dependency Methods ---------*/

	/*--------- Set by WebApplicationLoader.load() ---------*/
	// Usually set by WebApplicationLoader.load()
	void setName(String name) {
		this.name = name;
	}

	// Usually set by WebApplicationLoader.load()
	void setFolder(File folder) {
		this.folder = folder;
		// set the view folder
		viewFolder = new File(folder, Part.Type.t.prefix());
		// set the config folder
		configFolder = new File(folder, Part.Type.config.prefix());
	}

	// Set by the WebApplicationLoader.load().
	// Should be called when the Module is an application, and the path is
	// directly in the webapp
	// If this is called, setFolder should not
	void setViewFolder(File viewFolder) {
		this.viewFolder = viewFolder;
	}

	// Set by the WebApplicationLoader.load().
	// Should be called when the Module is an application, and the path is
	// directly in the webapp
	// If this is called, setFolder should not
	void setConfigFolder(File configFolder) {
		this.configFolder = configFolder;
	}

	/*--------- /Set by WebApplicationLoader.load() ---------*/

	/*--------- /Dependency Methods ---------*/

	/* --------- WebModule Life Cycle --------- */
	public void init() throws Exception {

        if(webParameterParsers != null) {
            for(WebParameterParser webParameterParser : webParameterParsers) {
                registerWebParameterParser(webParameterParser);
            }
        }

		if (webHandlers != null) {
			for (Object activeBean : webHandlers) {
				registerWebHandlerMethods(activeBean);
			}
		}
		if (webModuleLifecyle != null) {
			webModuleLifecyle.init();
		}
	}

	public void shutdown() throws Exception {
		if (webModuleLifecyle != null) {
			webModuleLifecyle.shutdown();
		}
	}

	/* --------- /WebModule Life Cycle --------- */

	/*--------- Registration Methods ---------*/

    private void registerWebParameterParser(WebParameterParser webParameterParser) {
        Class<? extends Annotation> annotation = webParameterParser.getAnnotationClass();
        if(webParameterParserMap.containsKey(annotation)) {
            throw new IllegalStateException("multiple web parameter parsers configured for annotation class : " + annotation.getName());
        }

        webParameterParserMap.put(annotation, webParameterParser);
    }

	private final void registerWebHandlerMethods(Object targetObject) throws Exception {
		Class c = targetObject.getClass();

		Method methods[] = c.getMethods();
        List<String> additionalLeafPaths = new ArrayList<String>();

		for (Method m : methods) {
			// Annotation[] as = m.getAnnotations();

			// --------- Register Web Action --------- //
			WebActionHandler action = m.getAnnotation(WebActionHandler.class);
			// if it is an action method, then, add the WebAction Object and
			// Method to the action Dic
			if (action != null) {
				registerWebAction(targetObject, m, action);
			}
			// --------- /Register Web Action --------- //

			// --------- Register Web Model --------- //
			WebModelHandler modelBuilder = m.getAnnotation(WebModelHandler.class);
			if (modelBuilder != null) {
				registerWebModel(targetObject, m, modelBuilder);

                // if this is for a leaf path, grab the startWith values  from the
                // the web model handler annotation.
                // todo - warn if startsWith has no entries which has no effect?
                if(modelBuilder.leafPath()) {
                    String[] leafPaths = modelBuilder.startsWith();

                    // make sure they all have trailing slashes...
                    for(int i = 0; i < leafPaths.length; i++) {
                        if(!leafPaths[i].endsWith("/")) {
                            leafPaths[i] += "/";
                        }
                    }

                    additionalLeafPaths.addAll(Arrays.asList(leafPaths));
                }
			}
			// --------- Register Web Model --------- //

			// --------- Register Web File --------- //
			WebFileHandler webFile = m.getAnnotation(WebFileHandler.class);
			if (webFile != null) {
				registerWebFile(targetObject, m, webFile);
			}
			// --------- /Register Web File --------- //
			
			WebExceptionHandler webExceptionHandler = m.getAnnotation(WebExceptionHandler.class);
			if (webExceptionHandler != null){
				registerWebExceptionHandler(targetObject,m,webExceptionHandler);
			}

			// --------- Register Web Template Directive --------- //
			WebTemplateDirectiveHandler webTemplateDirective = m.getAnnotation(WebTemplateDirectiveHandler.class);
			if (webTemplateDirective != null) {

				registerWebTemplateDirective(targetObject, m, webTemplateDirective);
			}
			// --------- /Register Web Template Directive --------- //

		}

        // if we have any declared leaf paths, add them into the array.  they come after
        // any injected leaf path values.
        if(additionalLeafPaths.size() > 0) {
            if(leafPaths != null) {
                additionalLeafPaths.addAll(0, Arrays.asList(leafPaths));
            }

            leafPaths = additionalLeafPaths.toArray(new String[additionalLeafPaths.size()]);
        }
	}

	private final void registerWebModel(Object webHandler, Method m, WebModelHandler webModel) {
		// System.out.println("Register WebModel " + getName() + " - " +
		// m.getName());

		WebModelHandlerRef webModelRef = new WebModelHandlerRef(webHandler, m, webParameterParserMap, webModel);
		webModelRefList.add(webModelRef);

		String startWithArray[] = webModel.startsWith();
		for (String startsWith : startWithArray) {
			webModelByStartsWithMap.put(startsWith, webModelRef);
		}
	}

	private final void registerWebAction(Object webHandler, Method m, WebActionHandler webAction) throws Exception {

		String actionName = webAction.name();
		// if the action does have an empty name, then, take the name of the
		// method
		if (actionName.length() == 0) {
			actionName = m.getName();
		}
		// try to get the actionObjectList from the actionDic
		WebActionHandlerRef actionRef = webActionDic.get(actionName);
		// if the WebActionRef already exist, throw an exception
		if (actionRef != null) {
			// AlertHandler.systemSevere(Alert.ACTION_NAME_ALREADY_EXIST,
			// actionName);
			throw new Exception("Action Name Already Exist: " + actionName);
		}
		// if not found, create an empty list
		// System.out.println("WebModule.registerWebAction: " + getName() + ":"
		// + actionName);
		// add this object and method to the list
		webActionDic.put(actionName, new WebActionHandlerRef(webHandler, m, webParameterParserMap, webAction));
	}

	private final void registerWebFile(Object webHandler, Method m, WebFileHandler webFile) {
		WebFileHandlerRef webFileRef = new WebFileHandlerRef(webHandler, m, webParameterParserMap, webFile);
		webFileList.add(webFileRef);
	}
	
	private final void registerWebExceptionHandler(Object webHandler, Method m, WebExceptionHandler webExceptionHandler) {
		WebExceptionHandlerRef webExcpetionHandlerRef = new WebExceptionHandlerRef(webHandler, m, webParameterParserMap, webExceptionHandler);
		webExceptionHanderMap.put(webExcpetionHandlerRef.getThrowableClass(), webExcpetionHandlerRef);
		//webFileList.add(webFileRef);
	}
	

	private final void registerWebTemplateDirective(Object webHandler, Method m,
			WebTemplateDirectiveHandler webTemplateDirective) throws Exception {
		String templateMethodName = webTemplateDirective.name();
		// if the action does have an empty name, then, take the name of the
		// method
		if (templateMethodName.length() == 0) {
			templateMethodName = m.getName();
		}

		WebTemplateDirectiveHandlerRef directiveRef = new WebTemplateDirectiveHandlerRef(webHandler, m, webParameterParserMap);
		TemplateDirectiveProxy directiveProxy = new TemplateDirectiveProxy(templateMethodName, directiveRef);
		templateDirectiveProxyList.add(directiveProxy);
	}

	/*--------- /Registration Methods ---------*/

	public void destroy() {

	}

}
