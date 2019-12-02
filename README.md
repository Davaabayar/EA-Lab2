# EA-Lab2
#Constructor based DI 

@Configuration
@ComponentScan("edu.mum.cs544")
@EnableAspectJAutoProxy
public class Config {
}

App.class
ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

CustomerService.java
package edu.mum.cs544;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements ICustomerService {   
   private ICustomerDAO customerDAO;
   private IEmailSender emailSender;

   public CustomerService(ICustomerDAO customerDAO, IEmailSender emailSender) {
      this.customerDAO = customerDAO;
      this.emailSender = emailSender;
      System.out.println("Constructor based DI, injected beans are: " + customerDAO + ", " +emailSender);
   }
}

#####Basic AOP
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

#####Bank Application 
#DI
1.	Write AppConfig.class using @Configuration, @ComponentScan("edu.mum.cs544.bank")
2.	Put Component based notation to classes we need DI.
@Component
public class Logger implements ILogger{

@Component
public class JMSSender implements IJMSSender{

@Component
public class CurrencyConverter implements ICurrencyConverter{
@Repository
public class AccountDAO implements IAccountDAO {

3.	Constructor based DI for AccountService.java
a.	@Service for AccountService
@Service
public class AccountService implements IAccountService {

b.	Remove previous constructor
c.	Add custom constructor
public AccountService(IAccountDAO accountDAO, ICurrencyConverter currencyConverter, IJMSSender jmsSender, ILogger logger) {
   System.out.println("Custom constructor");
   this.accountDAO = accountDAO;
   this.currencyConverter = currencyConverter;
   this.jmsSender = jmsSender;
   this.logger = logger;
}
 
#2.1 Log every call to any method in the bank.dao package (using the Logger).
1.	Add dependency
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjrt</artifactId>
  <version>1.9.2</version>
</dependency>
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjweaver</artifactId>
  <version>1.9.2</version>
</dependency>
2.	Enable JautoProxy in AppConfig
@Configuration
@ComponentScan("edu.mum.cs544.bank")
@EnableAspectJAutoProxy
public class AppConfig {
}
3.	Add new Aspect DaoLogAspect.java 
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
    public void log(JoinPoint joinPoint){
        iLogger.log("+++++++++++++++ DAO Log Advice called method = " + joinPoint.getSignature().getName());
    }
}

#2.2 Use the Spring StopWatch functionality to measure the duration of all service

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

#2.3 Log every JMS message that is sent (using the Logger)

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

#2.5 Be sure to inject the logger into the advice class.
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


