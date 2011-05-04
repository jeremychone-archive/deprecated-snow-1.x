package org.snowfk.web;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowfk.util.FileUtil;
import org.snowfk.util.MapUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class HttpWriter {
	static private Logger logger = LoggerFactory.getLogger(HttpWriter.class);
	
	public static int                   BUFFER_SIZE                 = 2048 * 2;
	 
	 
	private ServletContext servletContext;
	
	@Inject
	public HttpWriter(ServletContext servletContext){
		this.servletContext = servletContext;
	}
	
	/**
	 * Still experimental
	 * @param rc
	 * @param fileName
	 * @param content
	 * @param cache (for now, not supported, only no-cache)
	 * @param options
	 * @throws Exception
	 */
	public void writeStringContent(RequestContext rc, String fileName, Reader contentReader, boolean cache, Map options) throws Exception{
		
		setHeaders(rc,fileName,cache,options);

		// --------- Stream File --------- //
		Writer ow = null;
		int realLength = 0;
		try {

            // create the reader/writer
            ow = rc.getRes().getWriter();

            char[] buffer = new char[BUFFER_SIZE];
            int readLength = contentReader.read(buffer);

            while (readLength != -1) {
                realLength += readLength;
                ow.write(buffer, 0, readLength);
                readLength = contentReader.read(buffer);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
        	contentReader.close();
        	if (ow != null){
        		ow.close();
        	}
        }	
	}
	
	public void writeBinaryContent(RequestContext rc,String fileName, InputStream contentIS,boolean cache, Map options) throws Exception{
		 OutputStream os = rc.getRes().getOutputStream();
		 
		 BufferedInputStream bis = new BufferedInputStream(contentIS);

		 int realLength = 0;
		 
         try {
             byte[] buffer = new byte[BUFFER_SIZE];
             int len = buffer.length;
             while (true) {
                 len = bis.read(buffer);
                 realLength += len;
                 if (len == -1)
                     break;
                 os.write(buffer, 0, len);
             }

         } catch (Exception e) {
             logger.error(e.getMessage());
         } finally {
             os.close();
             contentIS.close();
             bis.close();
         }
	}
	
	
	
	private void setHeaders(RequestContext rc, String fileName, Boolean cache, Map options) throws Exception{
		HttpServletRequest req = rc.getReq();
		HttpServletResponse res = rc.getRes();

		String characterEncoding = MapUtil.getNestedValue(options, "characterEncoding");
		characterEncoding = (characterEncoding != null)?characterEncoding:"UTF-8";
		
		String contentType = MapUtil.getNestedValue(options, "contentType");
		contentType = (contentType != null)?contentType:servletContext.getMimeType(fileName);
		contentType = (contentType != null)?contentType:FileUtil.getExtraMimeType(fileName);
		
		
		req.setCharacterEncoding(characterEncoding);
		res.setContentType(contentType);
		
		// TODO: needs to support "cache=true"
		
		
		// --------- Set Cache --------- //
		res.setHeader("Pragma", "No-cache");
		res.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
		res.setDateHeader("Expires", 1);
		// --------- Set Cache --------- //		
	}
	

}
