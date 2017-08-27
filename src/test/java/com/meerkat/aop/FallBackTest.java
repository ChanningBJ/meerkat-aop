package com.meerkat.aop;

import junit.framework.Assert;
import org.springframework.stereotype.Component;

/**
 * Created by chengmingwang on 8/27/17.
 */
@Component
public class FallBackTest {

    public static class FallBackHandler extends FallBack {
         public int getFallBack(){
            Assert.assertEquals("Test Message", this.getException().getMessage());
            return 10;
         }

        public Long getFallBack(Long a){
            return a+1;
        }
    }

    @MeerkatCommand(fallBack = FallBackHandler.class)
    int execException() throws Exception {
        throw new Exception("Test Message");
    }

    @MeerkatCommand(fallBack = FallBackHandler.class)
    Long execNull(Long a) throws Exception {
        return null;
    }


    public static class Inspection implements ReturnValueInspection {

        public boolean isSuccess(Object returnValue) {
            Long result = (Long) returnValue;
            return result!=0;
        }
    }

    @MeerkatCommand(fallBack = FallBackHandler.class, returnValueInspection = Inspection.class)
    Long execInspection(Long a) throws Exception {
        return a-1;
    }
}
