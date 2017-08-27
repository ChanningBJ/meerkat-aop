package com.meerkat.aop;

import java.lang.annotation.*;

/**
 * Created by chengmingwang on 8/26/17.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MeerkatFusingCommand{
}
