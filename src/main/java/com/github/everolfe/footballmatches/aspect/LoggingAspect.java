package com.github.everolfe.footballmatches.aspect;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.github.everolfe.footballmatches.service.*.*(..))")
    private void allServiceMethods() {
    }

    @Pointcut("@annotation(AspectAnnotaion)")
    private void callServiceAnnotation() {
    }

    @Before(value = "callServiceAnnotation()")
    public void logBefore(final JoinPoint joinPoint) {
        if (shouldLog(joinPoint)) {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getSignature().getDeclaringTypeName();
            Object[] args = joinPoint.getArgs();
            LOGGER.info(">> {}.{}() - {}", className, methodName, Arrays.toString(args));
        }
    }

    private boolean shouldLog(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        return methodName.startsWith("update");
    }

    @AfterReturning(value = "callServiceAnnotation()", returning = "result")
    public void logAfter(final JoinPoint joinPoint, final Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        LOGGER.info("<< {}.{}() - {}", className, methodName, result);

    }

    @AfterThrowing(pointcut = "callServiceAnnotation()", throwing = "exception")
    public void logException(final JoinPoint joinPoint, final Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        LOGGER.error("<< {}.{}() - {}", className, methodName, exception.getMessage());
    }

    @Around(value = "callServiceAnnotation()")
    public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // Выполнение метода
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        LOGGER.info("== {}.{}() executed in {} ms", className, methodName, executionTime);

        return result;
    }

    @PostConstruct
    public void initAspect() {
        LOGGER.info("Aspect is initialized");
    }

    @PreDestroy
    public void destroyAspect() {
        LOGGER.info("Aspect is destroyed");
    }
}
