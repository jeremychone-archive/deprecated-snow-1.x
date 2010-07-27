/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer;

import java.io.Writer;

import net.sf.json.JSONSerializer;

import org.snowfk.web.part.Part;

import com.google.inject.Singleton;



/**
 * 
 * Render a part as a json String. If data is null, then, just render an empty JSON '{}'.
 * 
 * @author Jeremy Chone
 * @date Jul 31, 2009
 */
@Singleton
public class JsonRenderer implements Renderer {

    @Override
    public void processPart(Part part, Object data, Writer out) throws Exception {
        String jsonString;

        if (data == null) {
            jsonString = "{}";
        } else {
            Object jsObj = JSONSerializer.toJSON(data);
            jsonString = jsObj.toString();
        }

        out.write(jsonString);

    }

}
