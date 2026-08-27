package com.mbsystems.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.mbsystems.userservice.service.*.*(..))")
    public void serviceMethods() {}

    @Before( "serviceMethods()" )
    public void logBefore(JoinPoint joinPoint) {
        log.info( "Called service method: {} with arguments: {}",
                joinPoint.getSignature().getName(), joinPoint.getArgs() );
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info( "Service method: {}, returned: {}",
                joinPoint.getSignature().getName(), result);
    }
}
