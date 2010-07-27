/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.util;

import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

public class JsonUtil {

    public static String toJson(Object obj) {
        return toJson(obj,null);
    }
    
    public static String toJson(Object obj, String[] excludes) {
        JsonConfig c = new JsonConfig();
        if (excludes != null) {
            c.setExcludes(excludes);
        }
        
        Object jsObj = JSONSerializer.toJSON(obj, c);
        
        String result = jsObj.toString();
        

        return result;
    }
}
