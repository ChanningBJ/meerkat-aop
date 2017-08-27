package com.meerkat.aop;

import com.meerkat.fusing.FusingConfig;
import org.aeonbits.owner.Config;
import org.springframework.stereotype.Component;

/**
 * Created by chengmingwang on 8/27/17.
 */
@Component
public class FusingLogic {

    @Config.Sources("classpath:app.properties")
    public interface FusingConfig4Testing extends FusingConfig{}

    public static class FallbackHandler extends FallBack {
        public Integer getFallBack(){
            if(this.isFusing){
                return 1;
            } else {
                return -1;
            }
        }
    }


    @MeerkatCommand(
            fallBack = FusingLogic.FallbackHandler.class,
            fusingConfig = FusingConfig4Testing.class
    )
    public Integer command(){
        return null;
    }
}
