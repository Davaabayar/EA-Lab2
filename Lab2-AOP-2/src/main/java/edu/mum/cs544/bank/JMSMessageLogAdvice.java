package edu.mum.cs544.bank;

import edu.mum.cs544.bank.logging.ILogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JMSMessageLogAdvice {
    @Autowired
    private ILogger iLogger;

    @After("execution(* edu.mum.cs544.bank.jms.JMSSender.sendJMSMessage(..))")
    public void log(JoinPoint joinPoint){
        iLogger.log("+++++++++++++++ JMSMessageLogAdvice " + joinPoint.getSignature().getName() + " called, message: " + joinPoint.getArgs()[0]);
    }
}
