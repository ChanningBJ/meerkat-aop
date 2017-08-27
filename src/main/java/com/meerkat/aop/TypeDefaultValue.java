package com.meerkat.aop;

import java.util.HashMap;

/**
 * Created by chengmingwang on 8/27/17.
 */
public class TypeDefaultValue {

    private static HashMap<Class<?>,Object> defaultValue = new HashMap<Class<?>, Object>(){{
       put(long.class, new Long(0));
       put(Long.class, new Long(0));

       put(byte.class, new Byte("0"));
       put(Byte.class, new Byte("0"));

       put(short.class, new Short("0"));
       put(Short.class, new Short("0"));

       put(int.class, new Integer(0));
       put(Integer.class, new Integer(0));

       put(char.class, new Character('\u0000'));
       put(Character.class, new Character('\u0000'));

       put(float.class, new Float("0.0F"));
       put(Float.class, new Float("0.0F"));

       put(double.class, new Double("0.0"));
       put(Double.class, new Double("0.0"));

       put(Boolean.class, new Boolean(false));
       put(boolean.class, new Boolean(false));
    }};


    static Object getDefaultValue(Class<?> cls){
        return defaultValue.get(cls);
    }

}
