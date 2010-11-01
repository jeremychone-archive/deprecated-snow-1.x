package org.snowfk.web.method;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snowfk.util.FileUtil;
import org.snowfk.web.RequestContext;
import org.snowfk.web.method.argument.WebArgRef;

public class WebFileHandlerRef extends BaseWebHandlerRef implements PathMatcher {

    private WebFileHandler webFile;

    public WebFileHandlerRef(Object object, Method method, WebFileHandler webFile) {
        super(object, method);
        this.webFile = webFile;
        initWebParamRefs();
    }

    @Override
    public boolean matchesPath(String path) {
        String[] fileNameAndExt = FileUtil.getFileNameAndExtension(path);
        boolean match = false;
        //first match the ext.
        for (String ext : webFile.ext()) {
            
            if (ext.equalsIgnoreCase(fileNameAndExt[1])) {
                match = true;
                break;
            }
        }

        //if the match match, then, match the matches
        if (match) {
            for (String regex : webFile.matches()) {
                Pattern pat = Pattern.compile(regex);
                Matcher mat = pat.matcher(path);
                Boolean matches = mat.matches();
                if (matches) {
                    match = true;
                    break;
                }else{
                    match = false;
                }
            }
        }

        return match;
    }

    public Object invokeWebFile(RequestContext rc) throws Exception {
        Object[] paramValues = new Object[webArgRefs.size()];
        int i = 0;
        for (WebArgRef webParamRef : webArgRefs) {
            paramValues[i++] = webParamRef.getValue(rc);
        }
        return method.invoke(webHandler, paramValues);
    }

}
