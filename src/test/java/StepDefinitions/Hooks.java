package StepDefinitions;

import Funtions.CreateDriver;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.net.MalformedURLException;

public class Hooks {
    public static WebDriver driver;
    Logger log = Logger.getLogger(Hooks.class);
    Scenario scenario = null;

    @Before
    public void initDriver(Scenario scenario) throws IOException {
        log.info("*******************************************************************************************");
        log.info("[Configuracion] --- Inicializando Configuracion Driver");
        log.info("*******************************************************************************************");
        this.scenario = scenario;
        driver = CreateDriver.initConfig();

        log.info("*******************************************************************************************");
        log.info("[Esenario] - " + scenario.getName());
        log.info("*******************************************************************************************");
    }

    @After
    public void tearDown(){
        if(scenario.isFailed()){
            try{
                scenario.write("el esenario fallo");
                scenario.write("Current Page URL is " + driver.getCurrentUrl());
                byte[] screenShot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
                scenario.embed(screenShot, "src/test/resources/Data/Screenshots/Failed");
            }catch (WebDriverException somePlatformsDontSupportScreenshots){
                System.out.println(somePlatformsDontSupportScreenshots.getMessage());
            }
        }

        log.info("*******************************************************************************************");
        log.info("[Estado del driver] - Limpia y cierra esta instancia del driver");
        log.info("*******************************************************************************************");
        driver.quit();
    }
}
