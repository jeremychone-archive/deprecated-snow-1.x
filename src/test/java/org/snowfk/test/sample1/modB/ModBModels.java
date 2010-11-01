package org.snowfk.test.sample1.modB;

import java.util.List;
import java.util.Map;

import org.snowfk.web.db.hibernate.HibernateDaoHelper;

import org.snowfk.web.method.WebModelHandler;

import com.google.inject.Inject;

public class ModBModels {

    HibernateDaoHelper daoHelper;
    
    @Inject
    public ModBModels(HibernateDaoHelper daoHelper){
        this.daoHelper = daoHelper;
    }
    
    @SuppressWarnings("unchecked")
    @WebModelHandler(startsWith="/")
    public void allPages(Map<String,Object> m){
        List<Employee> employees = (List<Employee>) daoHelper.find(0, 10, "from Employee");
        m.put("employees", employees);
    }
}
