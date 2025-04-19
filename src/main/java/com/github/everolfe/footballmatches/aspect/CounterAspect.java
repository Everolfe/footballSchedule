package com.github.everolfe.footballmatches.aspect;

import com.github.everolfe.footballmatches.counter.RequestCounter;
import java.lang.reflect.Method;
import java.util.Optional;
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
    private static final String DEFAULT_HTTP_METHOD = "GET";
    private static final String PATH_SEPARATOR = "/";
    private static final String ENDPOINT_RESOLUTION_ERROR = "Could not resolve endpoint key";


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
    }

    private String resolveEndpointKey(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            String basePath = resolveBasePath(method);
            String methodPath = resolveMethodPath(method);
            String httpMethod = resolveHttpMethod(method);

            String fullPath = buildFullPath(basePath, methodPath);
            return String.format("%s %s", httpMethod, fullPath);

        } catch (Exception e) {
            LOGGER.warn(ENDPOINT_RESOLUTION_ERROR, e);
            return joinPoint.getSignature().toShortString();
        }
    }

    private String resolveBasePath(Method method) {
        return Optional.ofNullable(method.getDeclaringClass().getAnnotation(RequestMapping.class))
                .map(RequestMapping::value)
                .filter(values -> values.length > 0)
                .map(values -> values[0])
                .orElse("");
    }

    private String resolveHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            return requestMapping.method().length > 0 ?
                    requestMapping.method()[0].name() :
                    DEFAULT_HTTP_METHOD;
        }
        return DEFAULT_HTTP_METHOD;
    }

    private String resolveMethodPath(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return getPathValue(method.getAnnotation(GetMapping.class).value());
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            return getPathValue(method.getAnnotation(PostMapping.class).value());
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            return getPathValue(method.getAnnotation(PutMapping.class).value());
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return getPathValue(method.getAnnotation(DeleteMapping.class).value());
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            return getPathValue(method.getAnnotation(RequestMapping.class).value());
        }
        return "";
    }

    private String getPathValue(String[] pathValues) {
        return pathValues.length > 0 ? pathValues[0] : "";
    }

    private String buildFullPath(String basePath, String methodPath) {
        StringBuilder pathBuilder = new StringBuilder();

        if (!basePath.isEmpty()) {
            pathBuilder.append(basePath);
        }

        if (!methodPath.isEmpty()) {
            if (!methodPath.startsWith(PATH_SEPARATOR) && !basePath.endsWith(PATH_SEPARATOR)) {
                pathBuilder.append(PATH_SEPARATOR);
            }
            pathBuilder.append(methodPath);
        }

        return pathBuilder.toString();
    }
}