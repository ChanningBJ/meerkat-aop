package com.meerkat.aop;

import org.springframework.stereotype.Component;

/**
 * Created by chengmingwang on 8/21/17.
 */

@Component
public class Logic {

    private Integer num = 0;

    @MeerkatCommand
    Integer exec10PercentNull(){
        num+=1;
        if(num%10==0){
            return null;
        } else {
            return num;
        }
    }



    public static class CustomInspection implements ReturnValueInspection{

        public boolean isSuccess(Object returnValue) {
            return returnValue==null;
        }
    }

    @MeerkatCommand(returnValueInspection = CustomInspection.class)
    Integer execCustomInspection(){
        num+=1;
        if(num%10==0){
            return null;
        } else {
            return num;
        }
    }

    @MeerkatCommand
    void execVoidReturn(){

    }

    @MeerkatCommand
    void execException() throws Exception {
        num+=1;
        if(num%10==0){
            throw new Exception();
        }
    }


    public static class Exception1 extends Exception {}
    public static class Exception2 extends Exception {}

    @MeerkatCommand(ignoredExceptions = Exception2.class)
    void execIgnoreException() throws Exception {
        num+=1;
        if(num%10==0){
            throw new Exception1();
        } else {
            throw new Exception2();
        }
    }
}
