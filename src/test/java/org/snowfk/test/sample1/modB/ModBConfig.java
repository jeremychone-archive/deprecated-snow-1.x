package org.snowfk.test.sample1.modB;


import org.snowfk.web.WebModuleConfig;


public class ModBConfig extends WebModuleConfig {

    @Override
    public Class[] provideEntityClasses() {
        return new Class[]{Employee.class};
    }

    @Override
    public Class[] provideWebHandlerClasses() {
        return new Class[]{EmployeeActions.class,ModBModels.class};
    }

    



    

}
