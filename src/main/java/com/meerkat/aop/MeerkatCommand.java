package com.meerkat.aop;

import java.lang.annotation.*;

/**
 * Created by chengmingwang on 8/21/17.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MeerkatCommand {
    Class<? extends ReturnValueInspection> returnValueInspection() default ReturnValueInspectionNULL.class;
    Class<? extends Throwable>[] ignoredExceptions() default {};
}
