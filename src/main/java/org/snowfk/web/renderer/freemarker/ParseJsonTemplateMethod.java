package org.snowfk.web.renderer.freemarker;

import static org.snowfk.web.renderer.freemarker.FreemarkerUtil.getParam;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.snowfk.util.FileUtil;
import org.snowfk.util.JsonUtil;
import org.snowfk.web.part.Part;
import org.snowfk.web.part.PartResolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@Singleton
public class ParseJsonTemplateMethod implements TemplateMethodModelEx {

    PartResolver partResolver;
    
    @Inject
    public ParseJsonTemplateMethod(PartResolver partResolver){
        this.partResolver = partResolver;
    }
    
    @Override
    public Object exec(List args) throws TemplateModelException {
        Map result = null;
        String toParse = getParam(args.get(0), String.class);

        if (toParse != null) {
            
            //today, assume it is a path to a part
            Part part = partResolver.getPartFromPri(toParse);
            
            File jsonFile = part.getResourceFile();
            
            if (jsonFile.exists()){
                String json = FileUtil.getFileContentAsString(jsonFile);
                result = JsonUtil.toMapAndList(json);
            }
        }
        
        return result;
        
    }

}
