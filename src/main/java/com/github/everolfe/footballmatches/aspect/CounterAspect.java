package com.github.everolfe.footballmatches.aspect;

import com.github.everolfe.footballmatches.counter.RequestCounter;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Aspect
@Component
public class CounterAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(CounterAspect.class);

    private final RequestCounter requestCounter;

    @Autowired
    public CounterAspect(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Pointcut("@annotation(com.github.everolfe.footballmatches.aspect.CounterAnnotation)")
    public void annotatedWithCounter() {}

    @Before("annotatedWithCounter()")
    public void countRequest(JoinPoint joinPoint) {
        String endpointKey = resolveEndpointKey(joinPoint);
        int count = requestCounter.incrementAndGet(endpointKey);

        LOGGER.info("Endpoint '{}' called {} times", endpointKey, count);
    }

    private String resolveEndpointKey(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // Получаем базовый путь из класса
            RequestMapping classMapping = method.getDeclaringClass()
                    .getAnnotation(RequestMapping.class);
            String basePath = classMapping != null && classMapping.value().length > 0
                    ? classMapping.value()[0]
                    : "";

            // Получаем путь и HTTP метод из аннотаций метода
            String methodPath = "";
            String httpMethod = "GET";

            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                methodPath = getMapping.value().length > 0 ? getMapping.value()[0] : "";
                httpMethod = "GET";
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                methodPath = postMapping.value().length > 0 ? postMapping.value()[0] : "";
                httpMethod = "POST";
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                PutMapping putMapping = method.getAnnotation(PutMapping.class);
                methodPath = putMapping.value().length > 0 ? putMapping.value()[0] : "";
                httpMethod = "PUT";
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                methodPath = deleteMapping.value().length > 0 ? deleteMapping.value()[0] : "";
                httpMethod = "DELETE";
            } else if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                methodPath = requestMapping.value().length > 0 ? requestMapping.value()[0] : "";
                httpMethod = requestMapping.method().length > 0
                        ? requestMapping.method()[0].name()
                        : "GET";
            }

            String fullPath = basePath + (methodPath.startsWith("/")
                    ? methodPath : "/" + methodPath);
            return httpMethod + " " + fullPath;

        } catch (Exception e) {
            LOGGER.warn("Could not resolve endpoint key", e);
            return joinPoint.getSignature().toShortString();
        }
    }

}