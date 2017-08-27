package com.meerkat.aop;


import com.meerkat.meter.MeterCenter;
import com.meerkat.meter.OperationMeter;
import junit.framework.Assert;
import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by chengmingwang on 8/21/17.
 */
public class MeerkatCommandAspectTest {


    @Configuration
    @ComponentScan({"com.meerkat.aop"})
    @EnableAspectJAutoProxy
    public static class Config {
    }

    private static AnnotationConfigApplicationContext context;



    @BeforeClass
    public static void setup(){
        BasicConfigurator.configure();
        MeterCenter.INSTANCE.setUpdaterCycleSecond(10000).setReporterCycleSecond(10000).
                init();
        context = new AnnotationConfigApplicationContext(Config.class);
    }


    @org.junit.Test
    public void testNullReturn() throws Exception {

        Logic logic = context.getBean(Logic.class);
        int counter = 0;
        for(int k=0; k<100; k++) {
            try {
                int data = logic.exec10PercentNull();
            } catch (Exception e){
                counter+=1;
            }
        }
        Assert.assertEquals(counter, 10);

        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(Logic.class, "exec10PercentNull", OperationMeter.class);
        Assert.assertEquals(10, meter.getFailureCounter());
        Assert.assertEquals(90, meter.getSuccessCounter());
    }

    @org.junit.Test
    public void testCustomResultInspection() throws Exception {

        Logic logic = context.getBean(Logic.class);
        for(int k=0; k<100; k++) {
            logic.execCustomInspection();

        }
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(Logic.class, "execCustomInspection", OperationMeter.class);
        Assert.assertEquals(90, meter.getFailureCounter());
        Assert.assertEquals(10, meter.getSuccessCounter());
    }

    @org.junit.Test
    public void testVoidReturn() throws Exception {

        Logic logic = context.getBean(Logic.class);
        for(int k=0; k<100; k++) {
            logic.execVoidReturn();

        }
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(Logic.class, "execVoidReturn", OperationMeter.class);
        Assert.assertEquals(0, meter.getFailureCounter());
        Assert.assertEquals(100, meter.getSuccessCounter());
    }

    @org.junit.Test
    public void testException() throws Exception {

        Logic logic = context.getBean(Logic.class);
        for(int k=0; k<100; k++) {
            try {
                logic.execException();
            } catch (Exception e){

            }

        }
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(Logic.class, "execException", OperationMeter.class);
        Assert.assertEquals(10, meter.getFailureCounter());
        Assert.assertEquals(90, meter.getSuccessCounter());
    }

    @org.junit.Test
    public void testIgnoreException() throws Exception {

        Logic logic = context.getBean(Logic.class);
        for(int k=0; k<100; k++) {
            try {
                logic.execIgnoreException();
            } catch (Exception e){

            }

        }
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(Logic.class, "execIgnoreException", OperationMeter.class);
        Assert.assertEquals(10, meter.getFailureCounter());
        Assert.assertEquals(90, meter.getSuccessCounter());
    }

}