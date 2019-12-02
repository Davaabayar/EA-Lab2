package edu.mum.cs544.bank;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class MeasureServiceAdvice {
    @Around("execution(* edu.mum.cs544.bank.service.*.*(..))")
    public Object measureDuration(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start(proceedingJoinPoint.getSignature().getName());
        Object retVal = proceedingJoinPoint.proceed();
        sw.stop();
        long totaltime = sw.getLastTaskTimeMillis();
        System.out.print("Duration => "+ proceedingJoinPoint.getTarget().getClass() + "." + proceedingJoinPoint.getSignature().getName() + "(");
        Object[] args = proceedingJoinPoint.getArgs();
        for(int i=0; i<args.length; i++){
            System.out.print(" " + args[i] + " ");
        }
        System.out.print(") = " + totaltime + "ms\n");
        return retVal;
    }
}
