/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer;

import java.io.Writer;



import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.snowfk.web.part.Part;

import com.google.inject.Singleton;


//import flexjson.JSONSerializer;



/**
 * 
 * Render a part as a json String. If data is null, then, just render an empty JSON '{}'.
 * 
 * @author Jeremy Chone
 * @date Jul 31, 2009
 */
@Singleton
public class JsonRenderer implements Renderer {
	static private final String[] excludes = {"stackTrace"}; 
    @Override
    public void processPart(Part part, Object data, Writer out) throws Exception {
        String jsonString;

        if (data == null) {
            jsonString = "{}";
        } else {
        	JsonConfig jsonConfig = new JsonConfig();
        	jsonConfig.setExcludes(excludes);
        	jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        	Object jsObj = JSONSerializer.toJSON(data,jsonConfig);
        	jsonString = jsObj.toString();
        	
        	/* 2010-10-20-Jeremy: trying to use flexjson, but to restrictive for now. 
        	 *                    Does not serialize map (even with the MapTransformer), not list.
        	 *                    Will give another try later.
        	 */
        	/*
        	flexjson.JSONSerializer serializer = new flexjson.JSONSerializer();
        	serializer.transform(new MapTransformer(){

				@Override
				public void transform(Object arg0) {
					// TODO Auto-generated method stub
					super.transform(arg0);
				}
        		
        	}, Map.class);
            jsonString = serializer.serialize( data );
            */
        }

        out.write(jsonString);
    }

}
