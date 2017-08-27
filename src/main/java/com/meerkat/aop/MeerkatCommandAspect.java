package com.meerkat.aop;

import com.codahale.metrics.Timer;
import com.meerkat.fusing.FusingMeter;
import com.meerkat.meter.MeterCenter;
import com.meerkat.meter.OperationMeter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by chengmingwang on 8/21/17.
 */

@Aspect
@Component
@Slf4j
public class MeerkatCommandAspect {
    /**
     * Will use class name and method name as meter name
     * By default, return null or throw an exception will be treated as failure
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(MeerkatCommand)")
    public Object aroundOperation(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodJoinPointInfo methodJoinPointInfo = new MethodJoinPointInfo(joinPoint);

        if(methodJoinPointInfo.isFusingEnabled()){
            return execWithFusing(methodJoinPointInfo);
        } else {
            return execWithoutFusing(methodJoinPointInfo);
        }
    }


    private Object runFallBack(MethodJoinPointInfo methodJoinPointInfo, boolean isFusing, Throwable exception, Object result) {
        Class<? extends FallBack> fallbackClass = methodJoinPointInfo.getFallBackClass();
        if (fallbackClass == FallBackDisabled.class) {
            return null;
        } else {
            try {
                Method method = fallbackClass.getMethod(FallBack.FALL_BACK_CLASS_NAME, methodJoinPointInfo.getParameterTypes());
                FallBack fallBack = fallbackClass.newInstance();
                fallBack.isFusing = isFusing;
                fallBack.e = exception;
                return method.invoke(fallBack, methodJoinPointInfo.getArgs());
            } catch (NoSuchMethodException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
                return null;
            } catch (IllegalAccessException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
                return null;
            } catch (InstantiationException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
                return null;
            } catch (InvocationTargetException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
                return null;
            }
        }
    }

    private Object execWithoutFusing(MethodJoinPointInfo methodJoinPointInfo) throws Throwable {
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(
                methodJoinPointInfo.getJoinClass(), methodJoinPointInfo.getMethodName(), OperationMeter.class);
        return exec( methodJoinPointInfo, meter);
    }


    private Object execWithFusing(MethodJoinPointInfo methodJoinPointInfo) throws Throwable {
        FusingMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(
                methodJoinPointInfo.getJoinClass(), methodJoinPointInfo.getMethodName(), FusingMeter.class,
                new FusingMeter.Builder(methodJoinPointInfo.getFusingConfig()));
        if(meter.isFusing()) {  //is fall back is not defined, will return null;
            return runFallBack(methodJoinPointInfo, true, null, null);
        } else {
            return exec( methodJoinPointInfo, meter);
        }

    }

    private Object exec(MethodJoinPointInfo methodJoinPointInfo, OperationMeter meter) throws Throwable {
        Timer.Context context = meter.startOperation();
        Object result = null;
        try {
            result = methodJoinPointInfo.getJoinPoint().proceed();
        } catch (Throwable e) {
            if (methodJoinPointInfo.shouldIgnoreThisException(e)) {
                meter.endOperation(context, OperationMeter.Result.SUCCESS);
            } else {
                meter.endOperation(context, OperationMeter.Result.FAILURE);
                return runFallBack(methodJoinPointInfo, false, e, null);
            }
            throw e;
        }


        if (methodJoinPointInfo.returnTypeIsVoid()) {
            meter.endOperation(context, OperationMeter.Result.SUCCESS);
        } else { //Return type is not void, will check return value
            ReturnValueInspection inspector = methodJoinPointInfo.createReturnValueInspection();
            if (inspector != null) {
                if (inspector.isSuccess(result)) {
                    meter.endOperation(context, OperationMeter.Result.SUCCESS);
                } else {
                    meter.endOperation(context, OperationMeter.Result.FAILURE);
                    return runFallBack(methodJoinPointInfo, false, null, result);
                }
            }
        }

        return result;
    }
}
