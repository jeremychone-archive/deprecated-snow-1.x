/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.part;

import org.snowfk.web.RequestContext;


public interface CustomFramePriPath {

    
    public String getFramePriPath(RequestContext rc,String pagePriPath,String framePriPath);
}
