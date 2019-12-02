# EA-Lab2
#####Constructor based DI 

Add @Configuration and @ComponentScan notations on Config class.
<pre>
@Configuration
@ComponentScan("edu.mum.cs544")
@EnableAspectJAutoProxy
public class Config {
}
</pre>
Create Application context from defined java config/App.class/
<pre>
ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
</pre>
Define custom constructor in CustomerService.java. Beans are created when ComponentScan works and injected in the constructor.
    
    package edu.mum.cs544;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    
    @Service
    public class CustomerService implements ICustomerService {   
       private ICustomerDAO customerDAO;
       private IEmailSender emailSender;
       
       //custom constructor
       public CustomerService(ICustomerDAO customerDAO, IEmailSender emailSender) {
          this.customerDAO = customerDAO;
          this.emailSender = emailSender;
          System.out.println("Constructor based DI, injected beans are: " + customerDAO + ", " +emailSender);
       }
    }

###Basic AOP
Tasks
<ol>
<li>Reconfigure the application so that whenever the sendMail method on the EmailSender is called, a log message is created (using an after advice AOP annotation).
</li>
<li>Now change the log advice in such a way that the email address and the message are logged as well. You should be able to retrieve the email address and the message through the arguments of the sendEmail() method. </li>
<li>Change the log advice again, this time so that the outgoing mail server is logged as well. The outgoingMailServer is an attribute of the EmailSender object, which you can retrieve through the joinpoint.getTarget() method. </li>
<li>Write a new advice that calculates the duration of the method calls to the DAO
    Object and outputs the result to the console.
</li>
</ol> 
Defined aspect for above mentioned tasks.
    
    @Aspect
    @Component
    public class LogAspect{
    @After("execution(* edu.mum.cs544.EmailSender.sendEmail(..))")
    public void logAfter(JoinPoint joinPoint){
        //A
        System.out.print(new Date() + " method=" + joinPoint.getSignature().getName());
        //B
        System.out.print(" address="+joinPoint.getArgs()[0] + " message=" + joinPoint.getArgs()[1]);
        //C
        System.out.print(" outgoing mail server=" + ((EmailSender) joinPoint.getTarget()).getOutgoingMailServer());
    }
    
    //Around method will be used to calculate duration of method execution
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

###Bank Application 
#####DI
Write AppConfig.class using @Configuration, @ComponentScan
       
    package edu.mum.cs544.bank;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.EnableAspectJAutoProxy;
    
    @Configuration
    @ComponentScan("edu.mum.cs544")
    @EnableAspectJAutoProxy
    public class Config {
    
    }
Put Component based notation to classes we need DI.
<pre>
@Component
public class Logger implements ILogger{...

@Component
public class JMSSender implements IJMSSender{...

@Component
public class CurrencyConverter implements ICurrencyConverter{...

@Repository
public class AccountDAO implements IAccountDAO {...
</pre>

Constructor based DI for AccountService.java. Use @Service annotation for AccountService
<pre>
@Service
public class AccountService implements IAccountService {...
</pre>

Remove previous constructor and add following custom constructor
<pre>
public AccountService(IAccountDAO accountDAO, ICurrencyConverter currencyConverter, IJMSSender jmsSender, ILogger logger) {
   System.out.println("Custom constructor");
   this.accountDAO = accountDAO;
   this.currencyConverter = currencyConverter;
   this.jmsSender = jmsSender;
   this.logger = logger;
}
</pre>
####2.1 Log every call to any method in the bank.dao package (using the Logger).
Add dependencies

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

Enable JautoProxy in AppConfig

    @Configuration
    @ComponentScan("edu.mum.cs544.bank")
    @EnableAspectJAutoProxy
    public class AppConfig {
    }

Add new Aspect DaoLogAspect.java 
<pre>
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
</pre>
####2.2 Use the Spring StopWatch functionality to measure the duration of all service
<pre>
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
</pre>
####2.3 Log every JMS message that is sent (using the Logger)
<pre>
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
</pre>
####2.5 Be sure to inject the logger into the advice class.
<pre>
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
</pre>