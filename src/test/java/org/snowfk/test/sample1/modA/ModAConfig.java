package org.snowfk.test.sample1.modA;

import org.snowfk.web.WebModuleConfig;
import org.snowfk.web.auth.AuthService;
import org.snowfk.web.db.hibernate.HibernateDaoHelper;
import org.snowfk.web.db.hibernate.HibernateDaoHelperImpl;
import org.snowfk.web.names.LeafPaths;

public class ModAConfig extends WebModuleConfig {

    @Override
    protected void configure() {
        bind(AuthService.class).to(ModAAuthService.class);
        bind(String[].class).annotatedWith(LeafPaths.class).toInstance(new String[] { "/leafPage" });
        bind(HibernateDaoHelper.class).to(HibernateDaoHelperImpl.class);
    }

    @Override
    public Class[] provideWebHandlerClasses() {
        Class[] activeBeanClasses = { WebBeanActions.class, WebBeanModels.class, ModAAuthService.class };
        return activeBeanClasses;
    }

}
