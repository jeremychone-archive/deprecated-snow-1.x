package org.snowfk.util;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.apache.commons.lang.*;

/**
 * Contributed by: Steve van Loben Sels, http://metapossum.com/ (Thank you!)
 *
 */
public class ClassesInPackageScanner extends PackageScanner<Class<?>> {


    public interface AcceptanceTest {
        public boolean acceptClass(Class<?> cls);
    }




    private boolean includeInnerClasses;
    private AcceptanceTest acceptanceTest;

    public ClassesInPackageScanner(String packageName, ClassLoader classLoader) {
        this(packageName, classLoader, false);
    }

    public ClassesInPackageScanner(String packageName, ClassLoader classLoader, boolean includeInnerClasses) {
        this(packageName, classLoader, includeInnerClasses, null);
    }

    public ClassesInPackageScanner(String packageName, ClassLoader classLoader, boolean includeInnerClasses, AcceptanceTest acceptanceTest) {
        super(packageName, classLoader);
        this.includeInnerClasses = includeInnerClasses;
        this.acceptanceTest = acceptanceTest;
    }


    protected Class<?> loadClassIfAcceptable(String className, Set<Class<?>> entries) {
        try {
            Class cls = Class.forName(className);
            if(acceptanceTest == null || acceptanceTest.acceptClass(cls)) {
                return cls;
            }
        }
        catch(ClassNotFoundException e) {}

        return null;
    }


    protected boolean acceptFilename(String fileName) {
        return fileName.endsWith(".class") && (includeInnerClasses || fileName.indexOf("$") < 0);
    }


    protected Class<?> getItemIfAcceptable(JarEntry entry, Set<Class<?>> entries) {
        String fileName = StringUtils.substringAfterLast(entry.getName(), File.separator);
        String classPackage = StringUtils.substringBeforeLast(entry.getName(), File.separator).replace(File.separatorChar, '.');
        if(acceptFilename(fileName)) {
            return loadClassIfAcceptable(classPackage + "." + StringUtils.substringBeforeLast(fileName, "."), entries);
        }
        return null;
    }

    protected Class<?> getItemIfAcceptable(File dir, String fileName, Set<Class<?>> entries) {
        if(acceptFilename(fileName)) {
            String fileNameAsClass = new File(dir, fileName).getAbsolutePath();
            int idx = fileNameAsClass.lastIndexOf(packageName.replace('.', File.separatorChar));
            if(idx > 0) {
                fileNameAsClass = fileNameAsClass.substring(idx);
            }

            return loadClassIfAcceptable(StringUtils.substringBeforeLast(fileNameAsClass.replace(File.separatorChar, '.'), "."), entries);
        }

        return null;
    }
}
