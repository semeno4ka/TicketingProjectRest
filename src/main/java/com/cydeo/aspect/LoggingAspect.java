package com.cydeo.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j //create Logger with annotation instead of creating an Object
public class LoggingAspect {
//  Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private String getUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //can get username, since we use now Keycloak, below is required
        SimpleKeycloakAccount userDetails=( SimpleKeycloakAccount)authentication.getDetails();//returns Object, needs casting
        return userDetails.getKeycloakSecurityContext().getToken().getPreferredUsername();
    }

    @Pointcut("execution(* com.cydeo.controller.ProjectController.*(..)) || execution(* com.cydeo.controller.TaskController.*(..))")
    public void anyProjectOrTaskControllerPC(){}

    @Before("anyProjectOrTaskControllerPC()")
    public void beforeAnyProjectOrTaskControllerPC(JoinPoint joinPoint){
        log.info("Before -> Method: {}, User: {}",
                joinPoint.getSignature().toShortString(),
                getUsername());
    }

    @AfterReturning(pointcut = "anyProjectOrTaskControllerPC()", returning = "results")
    public void afterReturningAnyProjectOrTaskControllerPC(JoinPoint joinPoint, Object results){
        log.info("After -> Method: {}, User: {}",
                joinPoint.getSignature().toShortString(),
                getUsername(),
                results.toString());
    }

    @AfterThrowing(pointcut = "anyProjectOrTaskControllerPC()", throwing = "exception")
    public void afterThrowingProjectOrTaskControllerPC(JoinPoint joinPoint, Exception exception){
        log.info("After Throwing -> Method: {}, User: {}",
                joinPoint.getSignature().toShortString(),
                getUsername(),
                exception.getMessage());
    }


}
