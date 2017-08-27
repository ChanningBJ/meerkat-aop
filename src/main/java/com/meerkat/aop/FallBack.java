package com.meerkat.aop;

/**
 * Created by chengmingwang on 8/27/17.
 */
public class FallBack {

    public static final String FALL_BACK_CLASS_NAME = "getFallBack";

    boolean isFusing = false;
    Throwable e = null;
    Object result;

    public boolean isFusing() {
        return isFusing;
    }

    public Throwable getException() {
        return e;
    }

    public Object getResult() {
        return result;
    }
}
