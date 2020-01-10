
import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.safronov.autotest.seven.java.builder.LocalTestNgRunner;
import ru.safronov.autotest.seven.java.builder.ProcessingThread;
import ru.safronov.autotest.seven.java.config.DriverManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.safronov.autotest.seven.java.references.Parser;
import ru.safronov.autotest.seven.java.references.Parser.ParserType;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "stepdefinitions"
)
public class Runner {
    LocalTestNgRunner runner;
    private List<ProcessingThread> runners;

    @BeforeClass
    public void setUpClass() {
        runner = new LocalTestNgRunner(this.getClass());
        runners = new ArrayList<>();
    }

    @DataProvider
    public Object[][] features() {
        Object[][] scenarios = runner.provideScenarios();
        ((PickleEventWrapper)scenarios[0][0]).getPickleEvent();
        return scenarios;
    }

    @Test
    public void numberedScenario() {
        Parser.getInstance(ParserType.STATIC).parse("src\\main\\resources\\PageXmlSources.xml");
        System.setProperty("scenario.data", "{test:ChromeDriver}");
        String scenariosData = System.getProperty("scenario.data");
        List<String> threadData = new ArrayList<>();
        for (String s : scenariosData.split("\\}\\{")) {
            String replaceAll = s.replaceAll("[{}]", "");
            threadData.add(replaceAll);
        }
        for (String o : threadData) {
            String[] numbers = o.replaceAll(":.+", "").split(",");
            String driverType = o.replaceAll(".+:", "");
            ArrayList<Object[]> filteredScenarios = new ArrayList();
            scenarios_loop:
            for (Object[] scenarios : runner.provideScenarios()) {
                ArrayList<Object> featureContents = new ArrayList<>();
                for (Object scenario : scenarios) {
                    if (scenario instanceof CucumberFeatureWrapper) {
                        if (featureContents.size() == 0) continue scenarios_loop;
                        featureContents.add(scenario);
                        break;
                    }
                    String scenarioName = ((PickleEventWrapper) scenario).getPickleEvent().pickle.getName().trim();
                    for (String number : numbers) {
                        if (scenarioName.startsWith(number.trim())) {
                            featureContents.add(scenario);
                            break;
                        }
                    }
                }
                filteredScenarios.add(featureContents.toArray());
            }
            final Object[][] results = new Object[filteredScenarios.size()][];
            for (int i = 0; i < filteredScenarios.size(); i++) {
                results[i] = filteredScenarios.get(i);
            }

            LocalTestNgRunner localTestNgRunner = new LocalTestNgRunner(this.getClass()) {
                @Override
                public Object[][] provideScenarios() {
                    return results;
                }
            };
            ProcessingThread processingThread = new ProcessingThread(driverType, localTestNgRunner);
            runners.add(processingThread);
        }

        for (ProcessingThread thread : runners) {
            thread.start();
        }
        for (ProcessingThread processingThread : runners) {
            try {
                processingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @AfterClass
    public void start() {
        for (ProcessingThread processingThread : runners){
            processingThread.finish();
        }
        DriverManager.drop();

    }






//    @Test(dataProvider = "features")
//    public void test(PickleEventWrapper pickleEventWrapper, CucumberFeatureWrapper cucumberFeature) throws Throwable {
//        CustomLogger.info(String.format("Запуск сценария %s", cucumberFeature.toString()));
//        runner.runScenario(pickleEventWrapper.getPickleEvent());
//    }
}
