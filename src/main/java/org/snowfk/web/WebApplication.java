/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowfk.SnowRuntimeException;
import org.snowfk.util.FileUtil;
import org.snowfk.web.db.hibernate.HibernateHandler;
import org.snowfk.web.method.WebActionRef;
import org.snowfk.web.method.WebFileRef;
import org.snowfk.web.method.WebModelRef;
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
	private String defaultModuleName;
	private String contentFolderPath;

	private FreemarkerRenderer freemarkerRenderer;
	private JsonRenderer jsonRenderer;
	private HibernateHandler hibernateHandler;
	private Map<String, WebModule> webModuleByName = new HashMap<String, WebModule>();

	private boolean initialized = false;

	// _this is a quick hack for PartResolver. There might be a better way, but
	// not sure.
	// feel free to suggest.
	private WebApplication _this = this;
	private PartResolver partResolver = new PartResolver() {
		public Part getPartFromPri(String pri) {
			return _this.getPart(pri);
		}
	};

	@Inject
	public WebApplication(@Nullable HibernateHandler hibernateHandler) {
		this.hibernateHandler = hibernateHandler;
	}

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
			Interceptor hibernateInterceptor = null;
			// look for the first HibernateInterceptor
			// FIXME: Need to support multi-interceptor. Right now, just support
			// one (take the first one)
			for (WebModule webModule : webModuleByName.values()) {
				hibernateInterceptor = webModule.getHibernateInterceptor();
				if (hibernateInterceptor != null) {
					break;
				}
			}

			// init hibernate if needed
			if (hibernateHandler != null) {
				for (WebModule webModule : webModuleByName.values()) {
					hibernateHandler.addEntityClasses(webModule.getEntityClasses());
				}
				hibernateHandler.initSessionFactory(hibernateInterceptor);
			}
			/*--------- /Initialize Hibernate ---------*/

			/*--------- Initialize WebModule ---------*/
			// init the web modules
			for (WebModule webModule : webModuleByName.values()) {
				try {
					webModule.init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
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
	public Collection<WebModule> getWebModules() {
		return webModuleByName.values();
	}

	public WebModule getWebModule(String moduleName) {
		if (moduleName != null) {
			return webModuleByName.get(moduleName);
		} else {
			return null;
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
	 * TODO: Must support the config: Type file override. First look at the
	 * content folder for config.., then, in the base folder.
	 * 
	 * @param pagePri
	 * @param framePri
	 * @return
	 */
	public Part getPart(String pagePri, String framePri) {
		/*--------- Get the pri info ---------*/
		Type partType = PriUtil.getPartType(pagePri);
		if (partType == null) {
			throw new RuntimeException("Wrong pri format, no part type: " + pagePri);
		}

		WebModule webModule = getWebModule(PriUtil.getModuleNameFromPri(pagePri));
		if (webModule == null) {
			throw new RuntimeException("Wrong pri format, unknown module name: " + pagePri);
		}
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
				partBaseFolder = new File(contentFolder, partType.prefix() + File.separatorChar + webModule.getName());
			} else {
				throw new RuntimeException(
						"Cannot support content part, contentFolderPath not defined (please defined it in the application.properties): "
								+ pagePri);
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

	public void processJsonPart(Part part, RequestContext rc) throws Exception {
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
	public void processFreemarkerPart(Part part, RequestContext rc) throws Exception {

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
		WebFileRef webFileRef = module.getWebFileRef(priPath);
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
	public WebActionResponse processWebAction(String air, RequestContext rc) throws Exception {
		String[] webModuleNameAndActionName = air.split(":");
		return processWebAction(webModuleNameAndActionName[0], webModuleNameAndActionName[1], rc);
	}

	public WebActionResponse processWebAction(String webModuleName, String webActionName, RequestContext rc)
			throws Exception {
		WebModule webModule = getWebModule(webModuleName);
		rc.pushCurrentWebModule(webModule);
		if (webModule == null) {
			throw new Exception("No webModule found for: " + webModuleName);
		}

		WebActionRef webActionRef = webModule.getWebActionRef(webActionName);
		if (webActionRef == null) {
			throw new SnowRuntimeException(Alert.NO_WEB_ACTION, "WebAction", webModuleName + ":" + webActionName);
		}

		// --------- Invoke Method --------- //
		WebHandlerMethodInterceptor methodInterceptor = webModule.getWebHandlerMethodInterceptor();
		boolean invokeWebAction = true;

		if (methodInterceptor != null) {
			invokeWebAction = methodInterceptor.before(webActionRef.getMethod(), rc);
		}

		Object result = null;
		if (invokeWebAction) {
			result = webActionRef.invokeWebAction(rc);
		}

		if (methodInterceptor != null) {
			methodInterceptor.after(webActionRef.getMethod(), rc);
		}
		// --------- /Invoke Method --------- //

		WebActionResponse response = new WebActionResponse(result);
		rc.pollCurrentWebModule();
		return response;
	}

	@SuppressWarnings("unchecked")
	public void proccessWebModels(String webModuleName, Map m, RequestContext rc) throws Exception {
		WebModule webModule = getWebModule(webModuleName);
		if (webModule == null) {
			throw new Exception("No webModule found for: " + webModuleName);
		}

		// get the rootModelBuilder
		WebModelRef rootWmr = webModule.getWebModelRef("/");
		if (rootWmr != null) {
			invokeWebModelRef(webModule, rootWmr, m, rc);
		}

		// Match and process the "startsWith" webModels
		String[] priPaths = rc.getCurrentPriPaths();
		StringBuilder pathBuilder = new StringBuilder();
		for (int i = 0; i < priPaths.length; i++) {
			String path = pathBuilder.append('/').append(priPaths[i]).toString();
			WebModelRef webModelRef = webModule.getWebModelRef(path);
			invokeWebModelRef(webModule, webModelRef, m, rc);
		}

		// Match and process the "matches" webModels
		List<WebModelRef> matchWebModelRefs = webModule.getMatchWebModelRef(pathBuilder.toString());
		for (WebModelRef webModelRef : matchWebModelRefs) {
			invokeWebModelRef(webModule, webModelRef, m, rc);
		}
	}

	private void invokeWebModelRef(WebModule webModule, WebModelRef webModelRef, Map m, RequestContext rc)
			throws Exception {

		if (webModelRef != null) {
			
			WebHandlerMethodInterceptor methodInterceptor = webModule.getWebHandlerMethodInterceptor();
			boolean invokeWebAction = true;

			if (methodInterceptor != null) {
				invokeWebAction = methodInterceptor.before(webModelRef.getMethod(), rc);
			}

			if (invokeWebAction) {
				webModelRef.invokeWebModel(m, rc);
			}

			if (methodInterceptor != null) {
				methodInterceptor.after(webModelRef.getMethod(), rc);
			}			
			
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
