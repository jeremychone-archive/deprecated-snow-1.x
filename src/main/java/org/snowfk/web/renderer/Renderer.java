/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer;

import java.io.Writer;

import org.snowfk.web.part.Part;



public interface Renderer {

    public void processPart(Part part, Object data, Writer out) throws Exception;
}
