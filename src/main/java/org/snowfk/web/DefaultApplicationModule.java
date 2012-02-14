package org.snowfk.web;


import org.snowfk.web.names.WebHandlerClasses;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import com.metapossum.utils.scanner.reflect.ClassesInPackageScanner;

public class DefaultApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(FramePathResolver.class).to(DefaultFramePathResolver.class);
        bind(ResourcePathResolver.class).to(DefaultResourcePathResolver.class);
        bind(ActionNameResolver.class).to(DefaultActionNameResolver.class);
        
    }

    
    @Provides
    @WebHandlerClasses
    public Class[] providesWebHandlerClasses(){
        
    }
    
    
    private Class[] scanForWebHandlerClasses(WebModuleConfig webModuleConfig) {
        Class[] webHandlerClasses = scanForClasses(webModuleConfig.getClass(), new ClassesInPackageScanner.AcceptanceTest() {
            @Override
            public boolean acceptClass(Class<?> cls) {
                for (Method method : cls.getDeclaredMethods()) {
                    if (method.getAnnotation(WebActionHandler.class) != null || method.getAnnotation(WebFileHandler.class) != null
                                            || method.getAnnotation(WebModelHandler.class) != null
                                            || method.getAnnotation(WebTemplateDirectiveHandler.class) != null
                                            || method.getAnnotation(WebExceptionHandler.class) != null) {
                        return true;
                    }
                }
                return false;
            }
        });

        return webHandlerClasses;
    }

    private Class[] scanForClasses(Class baseClass,
                            org.snowfk.util.ClassesInPackageScanner.AcceptanceTest acceptanceTest) {
        Set<Class<?>> classes;

        try {
            // Note: the mobules have the same classloader as the application,
            // so, the getClass().getClassLoader() if fine.
            classes = new ClassesInPackageScanner(baseClass.getPackage().getName(), getClass().getClassLoader(), false, acceptanceTest).scan(true);
        } catch (IOException e) {
            throw new IllegalStateException("unable to scan package for classes", e);
        }

        return classes.toArray(new Class[classes.size()]);

    }
    /*--------- /Privates ---------*/
}
