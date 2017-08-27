package com.meerkat.aop;

import com.codahale.metrics.Timer;
import com.meerkat.meter.MeterCenter;
import com.meerkat.meter.OperationMeter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(
                methodJoinPointInfo.getJoinClass(), methodJoinPointInfo.getMethodName(), OperationMeter.class);
        Timer.Context context = meter.startOperation();


        Object result = null;
        try{
            result = joinPoint.proceed();
        } catch (Throwable e){
            if(methodJoinPointInfo.shouldIgnoreThisException(e)){
                meter.endOperation(context, OperationMeter.Result.SUCCESS);
            } else {
                meter.endOperation(context, OperationMeter.Result.FAILURE);
            }
            throw e;
        }



        if(methodJoinPointInfo.returnTypeIsVoid()){ //Return type is not void, will check return value
            meter.endOperation(context, OperationMeter.Result.SUCCESS);
        } else {
            ReturnValueInspection inspector = methodJoinPointInfo.createReturnValueInspection();
            if(inspector!=null){
                if(inspector.isSuccess(result)){
                    meter.endOperation(context, OperationMeter.Result.SUCCESS);
                } else {
                    meter.endOperation(context, OperationMeter.Result.FAILURE);
                }
            }
        }

        return result;
    }
}
