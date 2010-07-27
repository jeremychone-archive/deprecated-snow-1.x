package org.snowfk.web.method;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) 
public @interface WebFile {
    
    String[] matches() default {};
    String[] ext() default {};
}
