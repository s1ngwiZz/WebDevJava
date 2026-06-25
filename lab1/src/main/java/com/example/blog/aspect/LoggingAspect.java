package com.example.blog.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.example.blog.controller..*)")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("Начало выполнения метода: {}.{}", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("Ошибка в методе {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;
        log.info("Метод {}.{} успешно выполнен за {} мс", className, methodName, executionTime);

        return result;
    }
}