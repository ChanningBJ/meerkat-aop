package com.meerkat.aop;


import com.meerkat.meter.MeterCenter;
import com.meerkat.meter.OperationMeter;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


/**
 * Created by chengmingwang on 8/21/17.
 */
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
        MeterCenter.INSTANCE.setUpdaterCycleSecond(20).setReporterCycleSecond(10000).
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

    @org.junit.Test
    public void testExceptionFallback() throws Exception{
        FallBackTest fallBackTest = context.getBean(FallBackTest.class);
        int result = fallBackTest.execException();
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(FallBackTest.class, "execException", OperationMeter.class);
        Assert.assertEquals(1, meter.getFailureCounter());
        Assert.assertEquals(10, result);
    }

    @org.junit.Test
    public void testNullFallback() throws Exception{
        FallBackTest fallBackTest = context.getBean(FallBackTest.class);
        long result = fallBackTest.execNull(10l);
        Assert.assertEquals(11l, result);
        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(FallBackTest.class, "execNull", OperationMeter.class);
        Assert.assertEquals(1, meter.getFailureCounter());
    }


    @org.junit.Test
    public void testexecInspectionFallback() throws Exception{
        FallBackTest fallBackTest = context.getBean(FallBackTest.class);
        long result = fallBackTest.execInspection(10l);
        Assert.assertEquals(9l, result);

        result = fallBackTest.execInspection(1l);
        Assert.assertEquals(2l, result);

        OperationMeter meter = MeterCenter.INSTANCE.getOrCreateMeter(FallBackTest.class, "execInspection", OperationMeter.class);
        Assert.assertEquals(1, meter.getFailureCounter());
        Assert.assertEquals(1, meter.getSuccessCounter());

    }

    @org.junit.Test(timeout = 30000)
    public void testFusingStepA() throws Throwable {
        FusingLogic fusingLogic = context.getBean(FusingLogic.class);

        Assert.assertEquals(-1, fusingLogic.command().intValue());
        while (fusingLogic.command() == -1) {
            Thread.sleep(10);
        }
    }

    @org.junit.Test(timeout = 60000)
    public void testFusingStepB() throws Throwable {
        FusingLogic fusingLogic = context.getBean(FusingLogic.class);
        Assert.assertEquals(1, fusingLogic.command().intValue());
        while (fusingLogic.command() == 1) {
            Thread.sleep(10);
        }
    }

    @org.junit.Test(timeout = 30000)
    public void testFusingStepC() throws Throwable{
        FusingLogic fusingLogic = context.getBean(FusingLogic.class);
        Assert.assertEquals(-1, fusingLogic.command().intValue());
        while (fusingLogic.command() == -1){
            Thread.sleep(10);
        }
    }
}