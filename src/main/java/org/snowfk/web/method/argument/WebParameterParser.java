package org.snowfk.web.method.argument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.snowfk.web.RequestContext;

public interface WebParameterParser<A extends Annotation> {

    public Class<A> getAnnotationClass();

    public <T> T getParameterValue(Method m, A annotation, Class<T> paramType, RequestContext rc);
}
