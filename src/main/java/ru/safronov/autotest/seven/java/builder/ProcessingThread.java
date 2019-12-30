package ru.safronov.autotest.seven.java.builder;

import cucumber.api.testng.PickleEventWrapper;
import ru.safronov.autotest.seven.java.config.DriverManager;
import ru.safronov.autotest.seven.java.utils.CustomLogger;

import java.util.UUID;

public class ProcessingThread extends Thread {
    private LocalTestNgRunner runner;
    private String sessionId;
    private String driverType;
    public ProcessingThread(String driverType, LocalTestNgRunner runner) {
        this.runner = runner;
        this.driverType=driverType;
    }
    @Override
    public void run() {
        sessionId = UUID.randomUUID().toString();
        try {
            DriverManager.setWD(sessionId, DriverManager.createWD(driverType));
        } catch (Exception e){
            CustomLogger.fail(sessionId,e);
            return;
        }
        Object[][] scenarios = runner.provideScenarios();
        try {
            for(Object[] scenario: scenarios) {
                runner.runScenario(((PickleEventWrapper)scenario[0]).getPickleEvent());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        PageMapper.drop(sessionId);
    }


    public void finish() {
        runner.finish();
    }

    public static String getSessionId(){
        return ((ProcessingThread)Thread.currentThread()).sessionId;
    }
}
