/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.db.hibernate;

import org.aspectj.lang.annotation.SuppressAjWarnings;


public aspect ATransactional {

    /*
     * Matches the execution of any public method in a type with the
     * Transactional annotation, or any subtype of a type with the Transactional
     * annotation.
     * SEE: http://fisheye1.atlassian.com/browse/springframework/spring/aspectj/src/org/springframework/transaction/aspectj/AnnotationTransactionAspect.aj?r=HEAD
      private pointcut executionOfAnyPublicMethodInAtTransactionalType() :
         execution(public * ((@Transactional *)+).*(..)) && @this(Transactional);
         
     */

    /**
     * The execution of any method that has the @Tx annotation
     */
    
    pointcut transactionalMethodExecution(Transactional transactional) :
        execution(* *(..)) && @annotation(transactional);

    /**
     * Placeholder for implementing tx policies  
     */    
    @SuppressAjWarnings({"adviceDidNotMatch"})
    Object around(Transactional transactional) : transactionalMethodExecution(transactional) {

        //get the sessionHolder
        SessionHolder sessionHolder = SessionHolder.getThreadSessionHolder();

        boolean txOwner = false;
        //// beginTransaction if necessary
        //if there is no transaction open, then, begin the transaction, 
        //and set this call as the txOwner        
        if (!sessionHolder.isTxOpen()) {
            //System.out.println(".......... Transaction Start");
            sessionHolder.beginTransaction();
            txOwner = true;
        }

        Object ret = null;
        try {
            
            
            //// proceed to call
            //System.out.println("..... before");
            ret = proceed(transactional);
            //System.out.println("..... after");
            //// if this call had begun the transaction, then commit it
            if (txOwner) {
                sessionHolder.commitTransaction();
                //System.out.println(".......... Transaction Commit");
            }

        } catch (Throwable t) {
            
            t.printStackTrace();
            SnowHibernateException she = new SnowHibernateException(t);
            System.out.flush();
            System.out.println("SnowHibernateException message: " + she.getMessage());
            //// if this call had begun the transaction, then 
            //   do a rollback on exception
            if (txOwner) {
                sessionHolder.rollbackTransaction();
                System.out.println(".......... Transaction Rollback");
            }
            throw new SnowHibernateException(she);
        }
        
        
        return ret;
    }
}
