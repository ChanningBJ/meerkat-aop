package com.meerkat.aop;

import com.meerkat.fusing.FusingConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by chengmingwang on 8/26/17.
 */
@Slf4j
public class MethodJoinPointInfo {

    private final MeerkatCommand annotation;
    private final Class[] parameterTypes;
    private ProceedingJoinPoint joinPoint;

    Class<? extends Object> joinClass;
    private Method method;


    MethodJoinPointInfo(ProceedingJoinPoint joinPoint){
        this.joinPoint = joinPoint;

        // Get class info
        joinClass = joinPoint.getTarget().getClass();

        // Get method info
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        this.method = methodSignature.getMethod();
        this.parameterTypes = methodSignature.getParameterTypes();


        // Get annonation INFO
        annotation = this.method.getAnnotation(MeerkatCommand.class);
//        this.returnValueInspection = annotation.returnValueInspection();
//        this.ignoreExceptions = annotation.ignoredExceptions();


    }


    Class<? extends Object> getJoinClass(){
        return this.joinClass;
    }

    String getMethodName(){
        return this.method.getName();
    }

    boolean returnTypeIsVoid(){
        Class<?> returnType = this.method.getReturnType();
        return void.class == returnType || Void.class == returnType;
    }

    Class<?> getReturnType(){
        return this.method.getReturnType();
    }
    ReturnValueInspection createReturnValueInspection(){
        try {
            return this.annotation.returnValueInspection().newInstance();
        } catch (InstantiationException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (IllegalAccessException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    boolean shouldIgnoreThisException(Throwable e){
        if(this.annotation.ignoredExceptions().length==0){
            return false;
        }
        for(Class<? extends Throwable> exceptionClass : this.annotation.ignoredExceptions()){
            if(exceptionClass.isInstance(e)){
                return true;
            }
        }
        return false;
    }

    boolean isFusingEnabled(){
        return this.annotation.fusingConfig()!=FusingDisabled.class;
    }

    boolean isFallBackEnabled(){
        return this.annotation.fallBack()!=FallBackDisabled.class;
    }

    Class<? extends FusingConfig> getFusingConfig(){
        return this.annotation.fusingConfig();
    }

    Class<? extends FallBack> getFallBackClass(){
        return this.annotation.fallBack();
    }


    Class[] getParameterTypes(){
        return this.parameterTypes;
    }

    Object[] getArgs(){
        return this.joinPoint.getArgs();
    }


    ProceedingJoinPoint getJoinPoint(){
        return this.joinPoint;
    }

}
