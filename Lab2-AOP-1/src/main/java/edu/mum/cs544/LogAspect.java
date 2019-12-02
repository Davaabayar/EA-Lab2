package edu.mum.cs544;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @After("execution(* edu.mum.cs544.EmailSender.sendEmail(..))")
    public void logAfter(JoinPoint joinPoint){
        //A
        System.out.print(new Date() + " method=" + joinPoint.getSignature().getName());
        //B
        System.out.print(" address="+joinPoint.getArgs()[0] + " message=" + joinPoint.getArgs()[1]);
        //C
        System.out.print(" outgoing mail server=" + ((EmailSender) joinPoint.getTarget()).getOutgoingMailServer());
    }

    //D
    @Around("execution(* edu.mum.cs544.CustomerDAO.*(..))")
    public Object invoke(ProceedingJoinPoint call) throws Throwable{
        StopWatch sw = new StopWatch();
        sw.start(call.getSignature().getName());
        Object returnVal = call.proceed();
        sw.stop();
        long totaltime = sw.getLastTaskTimeMillis();
        System.out.println("Time to execute "+call.getSignature().getName()+" = "+totaltime+" ms");
        return returnVal;
    }
}
