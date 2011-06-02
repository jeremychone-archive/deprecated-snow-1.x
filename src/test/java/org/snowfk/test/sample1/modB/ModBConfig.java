package org.snowfk.test.sample1.modB;


import org.snowfk.web.WebModuleConfig;
import org.snowfk.web.db.hibernate.HibernateDaoHelper;
import org.snowfk.web.db.hibernate.HibernateDaoHelperImpl;


public class ModBConfig extends WebModuleConfig {

    
    @Override
    protected void configure() {
        bind(HibernateDaoHelper.class).to(HibernateDaoHelperImpl.class);
    }
    
    @Override
    public Class[] provideEntityClasses() {
        return new Class[]{Employee.class};
    }

    @Override
    public Class[] provideWebHandlerClasses() {
        return new Class[]{EmployeeActions.class,ModBModels.class};
    }

    



    

}
