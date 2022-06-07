package StepDefinitions;

import Funtions.SeleniumFunctions;
import Funtions.CreateDriver;
import cucumber.api.Scenario;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.sikuli.script.FindFailed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class StepDefinitions {

    SeleniumFunctions functions = new SeleniumFunctions();

    WebDriver driver;

    public static boolean actual = Boolean.parseBoolean(null);

    /**** Atributo Login ******/
    Logger log = Logger.getLogger(StepDefinitions.class);

    public StepDefinitions(){
        driver = Hooks.driver;
    }

    @Given("^Sitio sin parametro")
    public void iAmInAppMainSite() throws IOException {
        String url = functions.readProperties("MainAppUrlBase");
        log.info("Navegacion a: " + url);
        driver.get(url);
        functions.page_has_loaded();
    }

    @Given("^I Got to Site (.*)")
    public void iGotToSite(String URL){
        log.info("Navegacion a: " + URL);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
        driver.get(URL);
        functions.page_has_loaded();
        functions.WindowsHandle("Principal");
    }

    @Then("^Close window")
    public void cerrarVentana(){
        driver.close();
    }

    @Then("^I Load The DOM Information (.*)$")
    public void cargarLaInformacionDOMAzloginJson(String json) throws Exception{
        SeleniumFunctions.FileName = json;
        SeleniumFunctions.readJson();
        log.info("Inicialize archivo: " + json);
    }


    @And("^I Do Click In Element (.*)")
    public void iDoClickInElement(String element) throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        functions.waitForElementPresent(element);
        driver.findElement(SeleniumElement).click();
        log.info("hizo click en : " + element);
    }

    @And("^I Set (.*) With Test (.*)")
    public void iSet(String element, String text) throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        driver.findElement(SeleniumElement).clear();
        driver.findElement(SeleniumElement).sendKeys(text);
        WebElement Selenium = driver.findElement(SeleniumElement);
        Selenium.sendKeys(Keys.ENTER);
        log.info("se coloco: "+ text + "del elemento:" + element);
    }

    @Given("^I set (.*) value in Data Scenario")
    public void iSetUserEmailValueInDataScenario(String parameter)throws IOException {
        functions.RetriveTestData(parameter);
    }

    @And("^I Save text of (.*?) as Scenario Context$")
    public void iSaveTextOfElementAsSenarioContext(String element) throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        String ScenarioElementText = driver.findElement(SeleniumElement).getText();
        functions.SaveInScenario(element+".text",ScenarioElementText);
    }

    @And("^I set (.*) with key value (.*)")
    public void iSeteEmailWithKeyValueTituloText(String element,String key) throws Exception{
        functions.iSetElementWithKeyValue(element,key);
    }

    @And("^I Set text (.*) in dropdown (.*)")
    public void iSetTextColombiaInDropdownCountry(String option, String element)throws Exception {
        Select opt = (Select) functions.selectOption(element);
        opt.selectByVisibleText(option);
    }

    @And("^I Set index (.*) in dropdown (.*)")
    public void iSetIndexInDropdownPais(int option,String element)throws Exception {
        Select opt = (Select) functions.selectOption(element);
        opt.selectByIndex(option);
    }


    @Then("^I check if (.*) error message is (.*)")
    public void iCheckIfErrorMessageIs(String element, String state)throws Exception {
        boolean actual = functions.isElementDisplayed(element);
        Assert.assertEquals("El estado es diferente al esperado", actual, Boolean.valueOf(state));
    }

    /** Cambio de frame interno **/
    @And("^I switch to Frame: (.*)")
    public void iSwitchToFrame(String frame)throws Exception{
        functions.switchToFrame(frame);
    }

    /** Cambio de frame al principal **/
    @And("^I switch to parent frame")
    public void iSwitchToParentFrame()throws Exception{
        functions.switchToParentFrame();
    }

    /** Dar Click en los Checks **/
    @And("^I check the checkbox having (.*)")
    public void iCheckTheCheckboxHaving(String element) throws Exception{
        functions.checkCheckBox(element);
    }

    /** Dar click en el JS **/
    @And("^I Do Click in JS element (.*)")
    public void iDoClickInJSElement(String element)throws Exception {
        functions.ClickJSElement(element);
    }

    /** Espera el elemento**/
    @And("^I wait for element (.*)")
    public void iWaitForElement(String element)throws Exception {
        functions.waitForElementPresent(element);
    }

    @And("^I scroll to element (.*)")
    public void scrollToElement(String element)throws Exception{
        functions.scrolltoElement(element);
    }

    @And("^I scroll to (top|end) of pages")
    public void scrollPage(String to)throws Exception{
        functions.scrollPage(to);
    }

    @Given("^I open new tab with URL (.*)")
    public void OpenNewTabWithURL(String url){
        functions.openNewTabWithURL(url);
    }

    /** Cambiando a una nueva ventana **/
    @When("^I switch to new window")
    public void switchNewWindow(){
        System.out.println(driver.getWindowHandles());
        for(String winHandle : driver.getWindowHandles()){
            System.out.println(winHandle);
            log.info("Cambiando a nueva ventana");
            driver.switchTo().window(winHandle);
            System.out.println("abrir nuevo tab con la URL: ");
        }
    }

    /** Otra forma mas extensa de cambiar a una ventana **/
    @When("^I go to (.*) window")
    public void switchNewNameWindow(String WindowsName){
        functions.WindowsHandle(WindowsName);
    }

    /** colocar tiempo de espera **/
    @And("^I wait (.*) seconds")
    public void iWaitSeconds(int seconds)throws InterruptedException{
        int secs = seconds*1000;
        Thread.sleep(secs);
    }

    @Then("^I (accept|dismiss) alert")
    public void AcceptAlert(String want){
        functions.AcceptAlert(want);
    }

    @And("^I take screenshoot: (.*)")
    public void iTakeScreenshot(String TestCaptura) throws IOException{
        functions.ScreenSchot(TestCaptura);
    }
    @And("^I attach a Screenshot to Report(.*)")
    public void attachAScreenShotToReport(String TestCaptura){
        functions.attachScreenShot(TestCaptura);
    }

    @And("^I click with Sikuli (.*)")
    public void searchImage(String element) throws FindFailed, IOException {
        functions.searchImage(element);
    }

    @Then("^Assert if (.*) contains text (.*)")
    public void assertIfContainsText(String element, String text)throws Exception {
        functions.checkPartialTextElementPresent(element, text);
    }

    @Then("^Assert if (.*) is equal to (.*)")
    public void assertIfIsEqualTo(String element, String text)throws Exception {
        functions.checkTextElementEqualTo(element, text);
    }

    @Then("^Check if (.*) is NOT contains text (.*)")
    public void CheckifIsNOTContainsText(String element, String text)throws Exception{
        functions.checkPartialTextElementNotPresent(element, text);
    }
    @Then("^Assert if (.*) is Displayed")
    public void CheckIfElementIsPresent(String element)throws Exception {
        boolean isDisplayed = functions.isElementDisplayed(element);
        Assert.assertTrue("El elemento esta presente" + element,isDisplayed);
    }
    @Then("^Check if (.*) Not is Displayed")
    public void CheckIfElementIsNotPresent(String element)throws Exception {

        boolean isDisplayed = functions.isElementDisplayed(element);
        Assert.assertFalse("El elemento NO esta presente" + element,isDisplayed);
    }

    @Then("^Subir Archivo (.*)")
    public void fileUpload(String element)throws Exception{
        functions.fileUpload(element);
    }


}
