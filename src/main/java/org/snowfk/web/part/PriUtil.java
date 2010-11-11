/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.part;

import org.snowfk.web.part.Part.Type;

public class PriUtil {

    public static Part.Type getPartType(String pri) {
        int idx = pri.indexOf(':');
        if (idx > -1) {
            String typeStr = pri.substring(0, idx);
            for (Part.Type type : Part.Type.values()) {
                if (typeStr.equals(type.name())) {
                    return type;
                }
            }
        }
        return null;
    }

    public static String getModuleNameFromPri(String pri) {
        if (pri != null && pri.indexOf(':') > -1) {
            String[] result = pri.split(":");
            return result[1];
        } else {
            return null;
        }
    }

    /**
     * @param pri
     * @return the path for this pri (the string after the last ':')
     */
    public static String getPathFromPri(String pri) {
        String path = pri;
        int idxOf = pri.lastIndexOf(':');
        if (idxOf != -1 && pri.length() > idxOf) {
            path = pri.substring(idxOf + 1);
        }
        //otherwise, the pri is the just path
        else{
            path = pri;
        }
        return path;

    }

    /**
     * @param pri
     * @param newPath
     * @return a new pri with the newPath
     */
    public static String updatePriPath(String pri, String newPath) {

        if (pri != null && newPath != null) {
            StringBuilder newPriSb = new StringBuilder();
            int idxOf = pri.lastIndexOf(':');
            newPriSb.append(pri.substring(0, idxOf));
            newPriSb.append(':');
            newPriSb.append(newPath);
            return newPriSb.toString();

        } else {
            return pri;
        }
    }

    public static String getRelativePartFilePath(String pri) {
        Type partType = PriUtil.getPartType(pri);
        String path = PriUtil.getPathFromPri(pri);

        String ext = null;

        // if there is no extension and the lastChar is not a '/', then, add the defaultExt
        char lastChar = path.charAt(path.length() - 1);
        int idxOfExt = path.lastIndexOf('.');

        if (idxOfExt == -1 && lastChar != '/') {
            ext = partType.defaultExt();
            return new StringBuilder(path).append(ext).toString();
        } else {
            return path;
        }
    }

}
