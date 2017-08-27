package com.meerkat.aop;

/**
 * Created by chengmingwang on 8/26/17.
 */

/**
 * Default inspection method, will tread the method execution as failure if return value is null
 */
public class ReturnValueInspectionNULL implements ReturnValueInspection {
    public boolean isSuccess(Object returnValue) {
        return returnValue!=null;
    }
}
