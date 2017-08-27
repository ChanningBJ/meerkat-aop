package com.meerkat.aop;

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

    private final Class<? extends Throwable>[] ignoreExceptions;
    private ProceedingJoinPoint joinPoint;

    Class<? extends Object> joinClass;
    private Method method;

    private Class<? extends ReturnValueInspection> returnValueInspection ;
    private static ReturnValueInspection defaultReturnValueInspection = new ReturnValueInspectionNULL();

    MethodJoinPointInfo(ProceedingJoinPoint joinPoint){
        this.joinPoint = joinPoint;

        // Get class info
        joinClass = joinPoint.getTarget().getClass();

        // Get method info
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        this.method = methodSignature.getMethod();


        // Get annonation INFO
        MeerkatCommand annotation = this.method.getAnnotation(MeerkatCommand.class);
        this.returnValueInspection = annotation.returnValueInspection();
        this.ignoreExceptions = annotation.ignoredExceptions();

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

    ReturnValueInspection createReturnValueInspection(){
        try {
            return this.returnValueInspection.newInstance();
        } catch (InstantiationException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (IllegalAccessException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    boolean shouldIgnoreThisException(Throwable e){
        if(this.ignoreExceptions.length==0){
            return false;
        }
        for(Class<? extends Throwable> exceptionClass : this.ignoreExceptions){
            if(exceptionClass.isInstance(e)){
                return true;
            }
        }
        return false;
    }

}
