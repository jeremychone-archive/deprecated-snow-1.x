package org.snowfk.test.sample1.modB;

import org.snowfk.web.RequestContext;
import org.snowfk.web.db.hibernate.HibernateDaoHelper;
import org.snowfk.web.method.WebAction;
import org.snowfk.web.method.WebParam;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EmployeeActions {

    private HibernateDaoHelper hibernateDaoHelper;

    @Inject
    public EmployeeActions(HibernateDaoHelper hibernateDaoHelper) {
        this.hibernateDaoHelper = hibernateDaoHelper;
    }

    @WebAction
    public Employee[] autoEmployeesFromParamMap(Employee employee1,@WebParam("employee2") Employee employee2) {
        return new Employee[]{employee1,employee2};
    }
    
    @WebAction
    public Employee saveEmployee(@WebParam("firstName") String firstName, @WebParam("username") String username, RequestContext rc) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setUsername(username);
        hibernateDaoHelper.save(employee);
        return employee;

    }
}
