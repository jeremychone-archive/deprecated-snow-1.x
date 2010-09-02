/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Interceptor;
import org.snowfk.web.auth.AuthService;
import org.snowfk.web.db.hibernate.HibernateDaoHelper;
import org.snowfk.web.method.WebAction;
import org.snowfk.web.method.WebActionRef;
import org.snowfk.web.method.WebFile;
import org.snowfk.web.method.WebFileRef;
import org.snowfk.web.method.WebModel;
import org.snowfk.web.method.WebModelRef;
import org.snowfk.web.method.WebTemplateDirective;
import org.snowfk.web.method.WebTemplateDirectiveRef;
import org.snowfk.web.names.EntityClasses;
import org.snowfk.web.names.LeafPaths;
import org.snowfk.web.names.WebHandlers;
import org.snowfk.web.part.CustomFramePriPath;
import org.snowfk.web.part.Part;
import org.snowfk.web.renderer.freemarker.TemplateDirectiveProxy;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.internal.Nullable;

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
	private Map<String, WebModelRef> webModelByStartsWithMap = new HashMap<String, WebModelRef>();
	private List<WebModelRef> webModelRefList = new ArrayList<WebModelRef>();
	private Map<String, WebActionRef> webActionDic = new HashMap<String, WebActionRef>();
	private List<WebFileRef> webFileList = new ArrayList<WebFileRef>();

	private List<TemplateDirectiveProxy> templateDirectiveProxyList = new ArrayList<TemplateDirectiveProxy>();

	// injected (optional)
	private String[] leafPaths;

	// injected (optional)
	private AuthService authService;

	// injected (optional)
	private Interceptor hibernateInterceptor;

	// injection (optional)
	private CustomFramePriPath customeFramePriPath;

	// injection (optional)
	private WebModuleLifeCycle webModuleLifecyle;

	// injection (optional)
	private RequestLifeCycle requestLifeCycle;
	
	// injection (optional)
	private WebHandlerMethodInterceptor webHandlerMethodInterceptor;

	// injection (optional)
	private HibernateDaoHelper hibernateDaoHelper;

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
	WebActionRef getWebActionRef(String actionName) {
		return webActionDic.get(actionName);
	}

	WebModelRef getWebModelRef(String path) {
		return webModelByStartsWithMap.get(path);
	}

	List<WebModelRef> getMatchWebModelRef(String fullPriPath) {
		List<WebModelRef> matchWebModelRefs = new ArrayList<WebModelRef>();

		for (WebModelRef webModelRef : webModelRefList) {
			// System.out.println("WebModule.getMatchWebModeulRef: " +
			// webModelRef.toString());
			boolean match = webModelRef.matchesPath(fullPriPath);
			if (match) {
				matchWebModelRefs.add(webModelRef);
			}
		}

		return matchWebModelRefs;
	}

	WebFileRef getWebFileRef(String path) {
		for (WebFileRef webFileRef : webFileList) {
			boolean match = webFileRef.matchesPath(path);
			if (match) {
				return webFileRef;
			}
		}
		return null;
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

	public Interceptor getHibernateInterceptor() {
		return hibernateInterceptor;
	}

	@Inject(optional = true)
	public void setHibernateInterceptor(Interceptor hibernateInterceptor) {
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
	
	public WebHandlerMethodInterceptor getWebHandlerMethodInterceptor(){
		return webHandlerMethodInterceptor;
	}
	
	@Inject(optional = true)
	public void setWebHandlerMethodInterceptor(WebHandlerMethodInterceptor webHandlerMethodInterceptor){
		this.webHandlerMethodInterceptor = webHandlerMethodInterceptor;
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

	private final void registerWebHandlerMethods(Object targetObject) throws Exception {
		Class c = targetObject.getClass();

		Method methods[] = c.getMethods();

		for (Method m : methods) {
			// Annotation[] as = m.getAnnotations();

			// --------- Register Web Action --------- //
			WebAction action = m.getAnnotation(WebAction.class);
			// if it is an action method, then, add the WebAction Object and
			// Method to the action Dic
			if (action != null) {
				registerWebAction(targetObject, m, action);
			}
			// --------- /Register Web Action --------- //

			// --------- Register Web Model --------- //
			WebModel modelBuilder = m.getAnnotation(WebModel.class);
			if (modelBuilder != null) {
				registerWebModel(targetObject, m, modelBuilder);
			}
			// --------- Register Web Model --------- //

			// --------- Register Web File --------- //
			WebFile webFile = m.getAnnotation(WebFile.class);
			if (webFile != null) {
				registerWebFile(targetObject, m, webFile);
			}
			// --------- /Register Web File --------- //

			// --------- Register Web Template Directive --------- //
			WebTemplateDirective webTemplateDirective = m.getAnnotation(WebTemplateDirective.class);
			if (webTemplateDirective != null) {

				registerWebTemplateDirective(targetObject, m, webTemplateDirective);
			}
			// --------- /Register Web Template Directive --------- //

		}
	}

	private final void registerWebModel(Object webHandler, Method m, WebModel webModel) {
		// System.out.println("Register WebModel " + getName() + " - " +
		// m.getName());

		WebModelRef webModelRef = new WebModelRef(webHandler, m, webModel);
		webModelRefList.add(webModelRef);

		String startWithArray[] = webModel.startsWith();
		for (String startsWith : startWithArray) {
			webModelByStartsWithMap.put(startsWith, webModelRef);
		}
	}

	private final void registerWebAction(Object webHandler, Method m, WebAction webAction) throws Exception {

		String actionName = webAction.name();
		// if the action does have an empty name, then, take the name of the
		// method
		if (actionName.length() == 0) {
			actionName = m.getName();
		}
		// try to get the actionObjectList from the actionDic
		WebActionRef actionRef = webActionDic.get(actionName);
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
		webActionDic.put(actionName, new WebActionRef(webHandler, m, webAction));
	}

	private final void registerWebFile(Object webHandler, Method m, WebFile webFile) {
		WebFileRef webFileRef = new WebFileRef(webHandler, m, webFile);
		webFileList.add(webFileRef);
	}

	private final void registerWebTemplateDirective(Object webHandler, Method m,
			WebTemplateDirective webTemplateDirective) throws Exception {
		String templateMethodName = webTemplateDirective.name();
		// if the action does have an empty name, then, take the name of the
		// method
		if (templateMethodName.length() == 0) {
			templateMethodName = m.getName();
		}

		WebTemplateDirectiveRef directiveRef = new WebTemplateDirectiveRef(webHandler, m);
		TemplateDirectiveProxy directiveProxy = new TemplateDirectiveProxy(templateMethodName, directiveRef);
		templateDirectiveProxyList.add(directiveProxy);
	}

	/*--------- /Registration Methods ---------*/

	public void destroy() {

	}

}
