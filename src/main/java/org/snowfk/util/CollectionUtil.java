/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class CollectionUtil {

    public static final Long[] emptyLongArray = new Long[0];
    
    
    public static <ITEM,ARG> ITEM findFirst(Collection<ITEM> collection, Closure<Boolean,ITEM,ARG> closure,ARG arg) {
        if (collection != null && closure != null) {
            for (Iterator<ITEM> iter = collection.iterator(); iter.hasNext();) {
                ITEM item = iter.next();
                if (closure.exec(item,arg)) {
                    return item;
                }
            }
        }
        return null;
    }
    
    public static <ITEM,ARG> List<ITEM> findAll(Collection<ITEM> collection, Closure<Boolean,ITEM,ARG> closure,ARG arg) {
        List<ITEM> list = new ArrayList<ITEM>();
        
        if (collection != null && closure != null) {
            for (Iterator<ITEM> iter = collection.iterator(); iter.hasNext();) {
                ITEM item = iter.next();
                if (closure.exec(item,arg)) {
                    list.add(item);
                }
            }
        }
        return list;
    }
    
    /**
     * Find the index of a value inside an array. If not found, or any of the param is null, return -1
     * @param array The array to lookup the value (if null, return -1)
     * @param value The value to lookup (if null, return -1)
     * @return
     */
    public static int findIndex(Object[] array,Object value){
        if (array == null || value == null){
            return -1;
        }
        int i = 0;
        for (Object obj : array){
            if (obj.equals(value)){
                return i;
            }
            i++;
        }
        return -1;
    }
    
    /**
     * Return a List of object (ArrayList) from an Array of objects
     * 
     * @param objs
     * @return
     */
    public static List<Object> getList(Object... objs) {
        List<Object> objects = new ArrayList<Object>();
        if (objs != null) {
            for (Object obj : objs) {
                objects.add(obj);
            }
        }
        return objects;
    }
    /**
     * Return a List of object (ArrayList) from an Array of objects
     * 
     * @param objs
     * @return
     */
    public static <T> List<T> getList(Class<T> cls,T... objs) {
        List<T> objects = new ArrayList<T>();
        if (objs != null) {
            for (T obj : objs) {
                objects.add(obj);
            }
        }
        return objects;
    }
}


