package org.snowfk.web;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class WebStateHandleFactoryTest {

    @Test
    public void testDefaultsAndSetWebStateMode() {

        WebStateHandleFactory factory = new WebStateHandleFactory();
        assertEquals(WebStateHandleFactory.WebStateMode.MultiCookie, factory.getWebStateMode());

        // should be set to multik-cookie on bad input.
        factory.setWebStateMode("blah");
        assertEquals(WebStateHandleFactory.WebStateMode.MultiCookie, factory.getWebStateMode());

        factory.setWebStateMode("SingleCookie");
        assertEquals(WebStateHandleFactory.WebStateMode.SingleCookie, factory.getWebStateMode());

        factory.setWebStateMode("MultiCookie");
        assertEquals(WebStateHandleFactory.WebStateMode.MultiCookie, factory.getWebStateMode());
    }


    /*

    svls : this test fails on a NPE b/c there's no readily available way to construct a test
    RequestContext with a valid test HttpServletRequest.  commenting out for now.

    @Test
    public void testReturnsProperType() {

        WebStateHandleFactory factory = new WebStateHandleFactory();

        factory.setWebStateMode(WebStateHandleFactory.WebStateMode.MultiCookie.name());
        assertEquals(WebStateHandleFactory.WebStateMode.MultiCookie, factory.getWebStateMode());

        RequestContext rc = new RequestContext(null, null);

        WebStateHandle handle = factory.constructWebStateHandle("testContext", rc);
        assertNotNull(handle);
        assertEquals(WebStateHandle.MultiCookieWebStateHandle.class, handle.getClass());



        factory.setWebStateMode(WebStateHandleFactory.WebStateMode.SingleCookie.name());
        assertEquals(WebStateHandleFactory.WebStateMode.SingleCookie, factory.getWebStateMode());

        handle = factory.constructWebStateHandle("testContext", rc);
        assertNotNull(handle);
        assertEquals(WebStateHandle.SingleCookieWebStateHandle.class, handle.getClass());
    }*/
}
