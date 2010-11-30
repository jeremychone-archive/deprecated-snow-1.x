package org.snowfk.web.renderer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.snowfk.util.FileUtil;
import org.snowfk.web.part.HttpPriResolver;
import org.snowfk.web.part.Part;

import com.google.inject.Singleton;

@Singleton
public class WebBundleManager {
    /**
     * Tells if a part is a reference to a WebBundler part. <br />
     * Note: as of now, we only support "web_bundle_all"
     * 
     * @param part
     *            (not null)
     * @return true if the part is a web_bundle reference
     */
    public boolean isWebBundlePart(Part part) {
        boolean r = false;

        String pri = part.getPri();
        String[] priPathAndExt = FileUtil.getFileNameAndExtension(pri);

        if (priPathAndExt[0].endsWith(HttpPriResolver.WEB_BUNDLE_ALL_PREFIX) && (priPathAndExt[1].equalsIgnoreCase(".js") || priPathAndExt[1].equalsIgnoreCase(".css"))) {
            r = true;
        }
        return r;
    }

    /**
     * Return the content of a webBundlePart. It will be look for the optional "all.bundle" in the folder to refine the
     * order/list if specified. <br />
     * TODO: Should we support streaming rather than putting the whole content in memory? <br />
     * Note: As of today, no server caching take place. Note that the content should be cache on each client.
     * 
     * @param webBundlePart
     * @return a String content.
     */
    public String getContent(Part webBundlePart) {
        String content = null;

        List<File> files = getWebBundleFiles(webBundlePart);

        StringBuilder contentSB = new StringBuilder();
        for (File file : files) {
            contentSB.append(FileUtil.getFileContentAsString(file));
            contentSB.append("\n");
        }
        content = contentSB.toString();
        return content;
    }

    /**
     * Return the list of files for the webBundlePart. The default is alphabetical order, but if there an "all.bundle" file in the same folder, 
     * it will be taken in consideration to include/order the files. 
     * 
     * TODO: Implement the all.bundle functionality to allow developers to easily customize what is included and the order.
     * 
     * @param webBundlePart
     * @return
     */
    public List<File> getWebBundleFiles(Part webBundlePart) {
        String fileExt = getFileExt(webBundlePart);
        File folder = webBundlePart.getResourceFile().getParentFile();
        return getWebBundleFiles(folder,fileExt);
    }
    
    public List<File> getWebBundleFiles(File folder, String fileExt){
        List<File> files = null;
        File[] allFiles = FileUtil.getFiles(folder, fileExt);

        //TODO: look at the "all.bundle" file for custom inclusion/ordering
        files = Arrays.asList(allFiles);

        return files;        
    }

    private String getFileExt(Part webBundlePart) {
        String pri = webBundlePart.getPri();
        String[] priPathAndExt = FileUtil.getFileNameAndExtension(pri);
        String fileExt = priPathAndExt[1];
        return fileExt;
    }
}
