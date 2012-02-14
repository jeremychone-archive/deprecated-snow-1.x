package org.snowfk.web.renderer.freemarker;

import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getParam;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.snowfk.util.FileUtil;
import org.snowfk.util.JsonUtil;
import org.snowfk.web.PathFileResolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@Singleton
public class ParseJsonTemplateMethod implements TemplateMethodModelEx {

    @Inject
    private PathFileResolver pathFileResolver;
    
    @Override
    public Object exec(List args) throws TemplateModelException {
        Map result = null;
        String path = getParam(args.get(0), String.class);

        if (path != null) {
            
            
            
            File jsonFile = pathFileResolver.resolve(path);
            
            if (jsonFile.exists()){
                String json = FileUtil.getFileContentAsString(jsonFile);
                result = JsonUtil.toMapAndList(json);
            }
        }
        
        return result;
        
    }

}
