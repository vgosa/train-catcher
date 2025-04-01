package org.group21;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.group21.exception.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Parameter;
import java.util.Optional;

@Component
@Aspect
@Slf4j
public class JwtValidationAspect {
    private final HttpServletRequest request;

    public JwtValidationAspect(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Validates the JWT token in the Authorization header through an aspect.
     * Add a {@link DecodedJWT} parameter to the target method if you need to access the token claims.
     * @param joinPoint Proceed the operation of the annotated target
     * @return The result of the target method
     * @throws Throwable If the JWT Token is missing, invalid or corrupted
     */
    @Around("@within(org.group21.annotations.RequiresAuthentication) || @annotation(org.group21.annotations.RequiresAuthentication)")
    public Object validateJwt(ProceedingJoinPoint joinPoint) throws Throwable {

        String token = Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .orElseThrow(() -> new UnauthenticatedException("Missing or invalid JWT Token in the Authorization header"));

        try {
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Object[] args = joinPoint.getArgs();
            Parameter[]  parameters = methodSignature.getMethod().getParameters();

            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getType().equals(DecodedJWT.class)) {
                    args[i] = jwt;
                    break;
                }
            }

            return joinPoint.proceed(args);
        } catch (JWTVerificationException e) {
            log.error("Invalid JWT Token", e);
            throw new UnauthenticatedException("Invalid JWT Token");
        }
    }
}
