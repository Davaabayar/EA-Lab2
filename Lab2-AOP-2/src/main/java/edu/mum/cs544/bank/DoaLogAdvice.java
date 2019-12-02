package edu.mum.cs544.bank;

import edu.mum.cs544.bank.logging.ILogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DoaLogAdvice {
    @Autowired
    private ILogger iLogger;

    @After("execution(* edu.mum.cs544.bank.dao.*.*(..))")
    public void logDAOCall(JoinPoint joinPoint){
        iLogger.log("+++++++++++++++ DAO Log Advice called method = " + joinPoint.getSignature().getName());
    }
}
