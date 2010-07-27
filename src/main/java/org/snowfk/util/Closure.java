/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.util;

public  interface  Closure<R,P,A>  {

    public R exec(P param,A arg);
}
