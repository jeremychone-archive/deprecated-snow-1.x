package org.snowfk.test.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.snowfk.util.MapUtil;

public class MapUtilTest {

    @Test
    public void testGetTreeMapValue(){
        Map treeMap = MapUtil.treeMapIt("user.name","jon","user.age",14L,"group","ace team");
        assertEquals("user.name","jon", MapUtil.getTreeMapValue(treeMap, "user.name", String.class, null));
        assertEquals("user.age int",(Integer)14, MapUtil.getTreeMapValue(treeMap, "user.age", Integer.class, null));
        assertEquals("user.age long",(Long)14L, MapUtil.getTreeMapValue(treeMap, "user.age", Long.class, null));
        assertEquals("group","ace team", MapUtil.getTreeMapValue(treeMap, "group", String.class, null));
    }
}
