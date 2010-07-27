/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.part;

import java.io.File;
import java.util.Set;

import org.snowfk.util.FileUtil;
import org.snowfk.util.MapUtil;
import org.snowfk.web.WebModule;




public class Part {
    
    static final Set freemarkerExts = MapUtil.setIt(".ftl");
    static final Set jsonExts = MapUtil.setIt(".json");
    static final Set textExts = MapUtil.setIt(".css",".js",".txt",".xml",".json");
    
    
    public enum Type {
        t("parts", ".ftl"), c("bymods", ".xml"), config("config", ".xml");

        private String prefix;
        private String defaultExt;

        Type(String prefix, String defaultExt) {
            this.prefix = prefix;
            this.defaultExt = defaultExt;
        }

        public String defaultExt() {
            return defaultExt;
        }

        public String prefix() {
            return prefix;
        }
    }

    public enum FormatType {
        freemarker, json, text, bin;
    }

    private Type       type;
    private FormatType formatType;

    private String     pri;
    private String     framePri;

    private String     resourcePath;
    private WebModule  webModule;
    
    
    public Part(String pagePri,String framePri,WebModule webModule, Type type){
        this.pri = pagePri;
        this.framePri = framePri;
        this.webModule = webModule;
        this.type = type;
    }
    
    public File getResourceFile() {
        if (resourcePath != null) {
            return new File(resourcePath);
        } else {
            return null;
        }
    }
    
    /*--------- Getters & Setters ---------*/
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    
    
    public FormatType getFormatType() {
        return formatType;
    }
    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }
    
    public String getPri() {
        return pri;
    }
    public void setPri(String pri) {
        this.pri = pri;
    }
    
    public String getFramePri() {
        return framePri;
    }
    public void setFramePri(String framePri) {
        this.framePri = framePri;
    }
    
  
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        if(resourcePath != null){
            String ext = FileUtil.getFileNameAndExtension(resourcePath)[1];
            if (ext != null){
                if (freemarkerExts.contains(ext)){
                    setFormatType(FormatType.freemarker);
                }else if (textExts.contains(ext)){
                    setFormatType(FormatType.text);
                }else{
                    setFormatType(FormatType.bin);
                }
                //NOTE that setResourcePath is not call if the FormatType is .json. 
            }
        }
    }
    
    public WebModule getWebModule() {
        return webModule;
    }
    public void setWebModule(WebModule webModule) {
        this.webModule = webModule;
    }
    /*--------- /Getters & Setters ---------*/
    


    

}
