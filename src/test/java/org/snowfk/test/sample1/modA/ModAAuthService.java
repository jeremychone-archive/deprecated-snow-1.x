package org.snowfk.test.sample1.modA;

import org.snowfk.web.RequestContext;
import org.snowfk.web.auth.Auth;
import org.snowfk.web.auth.AuthService;
import org.snowfk.web.method.WebAction;
import org.snowfk.web.method.WebParam;

import com.google.inject.Singleton;

@Singleton
public class ModAAuthService implements AuthService{

    /* This will be called by the controller.
     * @see org.snowfk.web.auth.AuthService#authRequest(org.snowfk.web.RequestContext)
     */
    @Override
    public Auth<?> authRequest(RequestContext rc) {
        System.out.println("MoadAAuthService authRequest()");
        return null;
    }
    
    @WebAction
    public void login(@WebParam("username") String username,@WebParam("password") String password){
        
    }


}
