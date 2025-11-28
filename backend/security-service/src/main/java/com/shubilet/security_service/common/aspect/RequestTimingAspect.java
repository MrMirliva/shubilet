package com.shubilet.security_service.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestTimingAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestTimingAspect.class);

    /**
     * Measures execution time of any method in:
     *  - controllers
     *  - services
     *  - repositories
     */
    @Around("""
            execution(* com.shubilet.security_service.controllers..*(..)) ||
            execution(* com.shubilet.security_service.sweeper..*(..))
            """)
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // method actually runs here

        long end = System.currentTimeMillis();

        long duration = end - start;

        log.info("[PERF] {}.{}() took {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    duration);

        return result;
    }
}
