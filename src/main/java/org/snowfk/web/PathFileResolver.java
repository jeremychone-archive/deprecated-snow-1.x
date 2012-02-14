package org.snowfk.web;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.snowfk.web.names.WebAppFolder;

@Singleton
public class PathFileResolver {

    @Inject 
    private @WebAppFolder File  webAppFolder;
    
    public File resolve(String path){
        return new File(webAppFolder,path);
    }
}
