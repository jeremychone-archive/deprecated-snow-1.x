/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.snowfk.util.FileUtil;
import org.snowfk.web.WebApplication;
import org.snowfk.web.part.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PartCacheManager {
    public enum Alert {
        CANNOT_FIND_PART_FOR_PRI,
        ERROR_WHILE_READING_CONTENT_FROM_FILE
    }

    private Map<String, PartsCache> partsCacheByPrisUid = new ConcurrentHashMap<String, PartsCache>();
    private Map<String, String>     prisUidByHref       = new ConcurrentHashMap<String, String>();

    private WebApplication webApplication;
    
    @Inject
    public void initDeps(WebApplication webApplication){
        this.webApplication = webApplication;
    }
    
    public final String getHrefForPartPris(List<String> pris,String contextPath) {
        String prisUid = buildPrisUid(pris,contextPath);

        // 1) get the enventual partsCache from this cache
        PartsCache partsCache = partsCacheByPrisUid.get(prisUid);

        if (partsCache == null) {
            partsCache = buildPartsCache(pris, contextPath,prisUid);
            putPartsCache(partsCache);
        }

        return partsCache.getHref();
    }

    public final String getContentForHref(String href) {
        PartsCache partsCache = getPartsCacheForHref(href);
        if (partsCache != null) {
            return buildContent(partsCache);
        } else {
            
            return null;
        }
    }

    private final PartsCache getPartsCacheForHref(String href) {
        PartsCache partsCache = null;
        String prisUid = prisUidByHref.get(href);
        if (prisUid != null) {
            partsCache = partsCacheByPrisUid.get(prisUid);
        }
        return partsCache;
    }

    private final void putPartsCache(PartsCache partsCache) {
        prisUidByHref.put(partsCache.getHref(), partsCache.getPrisUid());
        partsCacheByPrisUid.put(partsCache.getPrisUid(), partsCache);
    }

    private final PartsCache buildPartsCache(List<String> pris, String contextPath,String prisUid) {

        PartsCache partsCache = null;

        List<Part> parts = new ArrayList<Part>();
        String ext = null;

        // loop through too build the list of parts and find
        for (String pri : pris) {
            Part part = webApplication.getPart(pri);
            if (part != null) {
                // add to the parts list
                parts.add(part);

                File file = part.getResourceFile();

                // get the extension
                if (ext == null) {
                    ext = FileUtil.getFileNameAndExtension(file.getName())[1];
                }

            } else {
                throw new RuntimeException("Cannot find part for pri: "  + pri);
            }
        }

        // build the Href
        String href = new StringBuilder(contextPath).append(UUID.randomUUID().toString()).append(ext).toString();

        partsCache = new PartsCache();
        partsCache.setParts(parts);
        partsCache.setPris(pris);
        partsCache.setPrisUid(prisUid);
        partsCache.setHref(href);
        
        return partsCache;
    }

    private String buildContent(PartsCache partsCache) {
        List<Part> parts = partsCache.getParts();
        StringBuilder contentSb = new StringBuilder();

        
        for (Part part : parts) {


            File file = part.getResourceFile();
            // add the content
            String partContent = null;

            partContent = FileUtil.getFileContentAsString(file);
   
            if (partContent != null) {
                contentSb.append(partContent);
                contentSb.append("\n");
            }
        }
        return contentSb.toString();
    }

    private static final String buildPrisUid(List<String> pris,String contextPath) {
        StringBuilder sb = new StringBuilder(contextPath);
        sb.append('|');
        for (String pri : pris) {
            sb.append(pri);
            sb.append('|');
        }
        return sb.toString();
    }




}
