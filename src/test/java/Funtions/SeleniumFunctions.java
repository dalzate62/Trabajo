package Funtions;

import StepDefinitions.Hooks;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import javax.swing.text.Element;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.io.FilenameUtils.removeExtension;

public class SeleniumFunctions {

    WebDriver driver;
    public SeleniumFunctions(){driver = Hooks.driver;}

    private static Logger log = Logger.getLogger(SeleniumFunctions.class);
    public static Properties prop = new Properties();
    public static InputStream in = SeleniumFunctions.class.getResourceAsStream("../test.properties");

    /** Scenario Test Data **/
    public static Map<String, String> ScenaryData = new HashMap<>();
    public static Map<String, String> HandleMyWindows = new HashMap<>();
    public static String Environment = "";

    public static String PagesFilesPath = "src/test/resources/Pages/";
    public static String FileName = "";
    public static String GetFieldBy = "";
    public static String ValueToFind = "";
    public static int EXPLICIT_TIMEOUT = 15;
    public static String ElementText = "";
    public static String PathStore ="";
    public static boolean isDisplayed = Boolean.parseBoolean(null);

    public static Object readJson() throws Exception{
        FileReader reader = new FileReader(PagesFilesPath + FileName);
        try{
            if(reader != null){
                JSONParser jsonParser = new JSONParser();
                return jsonParser.parse(reader);
            }else{
                return null;
            }
        }catch (FileNotFoundException | NullPointerException e){
            log.error("ReadEntity: No existe el Archivo " + FileName);
            throw new IllegalStateException("ReadEntity: No existe el Archivo " +FileName, e);
        }
    }

    public static JSONObject ReadEntity(String element) throws Exception{
        JSONObject Entity = null;
        JSONObject jsonObject = (JSONObject) readJson();
        Entity = (JSONObject) jsonObject.get(element);
        log.info(Entity.toJSONString());
        return Entity;
    }

    public static By getCompleteElement(String element) throws Exception{
        By result = null;
        JSONObject Entity = ReadEntity(element);

        GetFieldBy = (String) Entity.get("GetFieldBy");
        ValueToFind = (String) Entity.get("ValueToFind");

        if("className".equalsIgnoreCase(GetFieldBy)){
            result = By.className(ValueToFind);
        }else if("cssSelector".equalsIgnoreCase(GetFieldBy)){
            result = By.cssSelector(ValueToFind);
        }else if("id".equalsIgnoreCase(GetFieldBy)){
            result = By.id(ValueToFind);
        }else if("linkText".equalsIgnoreCase(GetFieldBy)){
            result = By.linkText(ValueToFind);
        }else if("name".equalsIgnoreCase(GetFieldBy)){
            result = By.name(ValueToFind);
        }else if("link".equalsIgnoreCase(GetFieldBy)){
            result = By.partialLinkText(ValueToFind);
        }else if("tagName".equalsIgnoreCase(GetFieldBy)){
            result = By.tagName(ValueToFind);
        }else if("XPath".equalsIgnoreCase(GetFieldBy)) {
            result = By.xpath(ValueToFind);
        }
        return result;
    }

    public String readProperties(String property) throws IOException{
        prop.load(in);
        return prop.getProperty(property);
    }

    public void SaveInScenario(String key, String text){
        if(!this.ScenaryData.containsKey(key)){
            this.ScenaryData.put(key,text);
            log.info(String.format("guarda la clave del esenario: con un valor", key, text));
            System.out.println("guarda la clave del esenario: con un valor" + key + text);
        }
        else{
            this.ScenaryData.replace(key,text);
            log.info(String.format("Actualiza la clave del esenario: con un valor", key, text));
            System.out.println("Actualiza la clave del esenario: con un valor" + key + text);
        }
    }

    public void RetriveTestData(String parameter) throws IOException{
        Environment = readProperties("Environment");
        try {
            SaveInScenario(parameter,readProperties(parameter+"."+Environment));
            System.out.println("Este es el valor de la parametrizacion "+ parameter + " : "+ this.ScenaryData.get(parameter));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void iSetElementWithKeyValue(String element, String key) throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean exist = this.ScenaryData.containsKey(key);
        if(exist){
            String text = this.ScenaryData.get(key);
            driver.findElement(SeleniumElement).sendKeys(text);
            log.info(String.format("colocar el elemento con el texto ", element, text));
            System.out.println(String.format("colocar el elemento con el texto ", element, text));
        }else {
            Assert.assertTrue(String.format("No existe la key en el contexto", key),this.ScenaryData.containsKey(key));
        }
    }

    public ISelect selectOption(String element) throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("esperedo Elemento: " + element));
        Select opt = new Select(driver.findElement(SeleniumElement));
        return opt;
    }

    public void waitForElementPresent(String element)throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT );
        log.info("esperando el elemento: " + element + " a ser presentado");
        wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
    }
    public void waitForElementVisible(String element)throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT );
        log.info("esperando el elemento: " + element + " a ser presentado");
        wait.until(ExpectedConditions.visibilityOfElementLocated(SeleniumElement));
    }

    public void switchToFrame(String Frame)throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(Frame);
        log.info("cambiando de frame: " + Frame);
        driver.switchTo().frame(driver.findElement(SeleniumElement));
    }

    public void switchToParentFrame(){
        log.info("cambiando la pared del frame");
        driver.switchTo().parentFrame();
    }

    public void checkCheckBox(String element)throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean isChecked = driver.findElement(SeleniumElement).isSelected();
        if(!isChecked){
            log.info("Clicking sobre el check to select: " + element);
            driver.findElement(SeleniumElement).click();
            System.out.println("Clicking sobre el check to select: " + element);
        }else{
            log.info("Clicking sobre el check to select: " + element);
            driver.findElement(SeleniumElement).click();
        }
    }

    public void ClickJSElement(String element)throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        log.info("Click to element with js: " + element);
        jse.executeScript("arguments[0].click()", driver.findElement(SeleniumElement));
    }

    public void scrollPage(String to)throws Exception{
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        if(to.equals("top")){
            log.info("Scrolling to the top of the page");
            jse.executeScript("scroll(0, -250);");
        }else if(to.equals("end")){
            log.info("Scrolling to the end of the page");
            jse.executeScript("scroll(0, 250);");
        }
    }

    public void scrolltoElement(String element)throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        log.info("Scrolling to the element: " + element);
        jse.executeScript("arguments[0].scrollIntoView();",driver.findElement(SeleniumElement));
    }

    public void page_has_loaded(){
        String GetActual = driver.getCurrentUrl();
        System.out.println(String.format("Cheking si la pagina esta cargada. ", GetActual));
        log.info(String.format("Cheking si la pagina esta cargada. ", GetActual));
        new WebDriverWait(driver, EXPLICIT_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor)webDriver).executeScript("return document.readyState").equals("complete")
        );
    }

    public void openNewTabWithURL(String URL){
        log.info("abrir nuevo tab con la URL: " + URL);
        System.out.println("abrir nuevo tab con la URL: " + URL);
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript(String.format("window.open('%s','_black');",URL));
        System.out.println("abrir nuevo tab con la URL: " + URL);
    }

    public void WindowsHandle(String WindowsName){
        if(this.HandleMyWindows.containsKey(WindowsName)){
            driver.switchTo().window(this.HandleMyWindows.get(WindowsName));
            log.info(String.format("Voy ala ventana: con el valor ", WindowsName, this.HandleMyWindows.get(WindowsName)));
        }else{
            for (String winHandle: driver.getWindowHandles()){
                this.HandleMyWindows.put(WindowsName,winHandle);
                System.out.println("la nueva ventana " + WindowsName + " se salvo en el esenario con el valor " + this.HandleMyWindows.get(WindowsName));
                log.info("la nueva ventana " + WindowsName + " se salvo en el esenario con el valor " + this.HandleMyWindows.get(WindowsName));
                driver.switchTo().window(this.HandleMyWindows.get(WindowsName));
            }
        }
    }

    public void AcceptAlert(String want){
        try{
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            /** Colocar un switch si se encuentra una alerta diferente**/
            switch (want){
                case "accept":
                    alert.accept();
                    System.out.println("acepto la alerta");
                    break;
                case "dismiss":
                    alert.dismiss();
                    System.out.println("acepto la alerta");
                    break;
            }
            log.info("The alert was accepted successfully");
        }catch (Throwable e){
            log.error("Error came while waiting for the alert popup. " + e.getMessage());
        }
    }

    public void ScreenSchot(String TestCaptura) throws IOException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
        String screenShotName = readProperties("ScreenShotPath") + "\\" + readProperties("browser") + "\\" + TestCaptura + "_(" + dateFormat.format(GregorianCalendar.getInstance().getTime()) + ")";
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        log.info("Screenshot saved as: " + screenShotName);
        FileUtils.copyFile(scrFile, new File(String.format("%s.png", screenShotName)));
    }

    public byte[] attachScreenShot(String TestCaptura){
        log.info("atteching ScreenShot");
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(TestCaptura, new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
        return screenshot;
    }

    public String GetTextElement(String element) throws Exception{
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
        wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
        log.info(String.format("esperando el elemento: %s", element));

        ElementText = driver.findElement(SeleniumElement).getText();

        return ElementText;
    }

    public void checkPartialTextElementNotPresent(String element,String text)throws Exception{
        ElementText = GetTextElement(element);

        boolean isFoundFalse = ElementText.indexOf(text) !=-1 ?  true : false;
        Assert.assertFalse("este Texto esta presente en el elemento: " + element + "el texto actual es: " + ElementText, isFoundFalse);
    }

    public void checkPartialTextElementPresent(String element,String text)throws Exception{
        ElementText = GetTextElement(element);

        boolean isFound = ElementText.indexOf(text) !=-1 ? true : false;
        Assert.assertTrue("este Texto esta presente en el elemento: " + element + "el texto actual es: " + ElementText, isFound);

    }

    public void checkTextElementEqualTo(String element, String text) throws Exception{
        ElementText = GetTextElement(element);

        Assert.assertEquals("el texto no esta presente en el elemento: " + element + "el texto actual es: " + ElementText, text, ElementText);
    }

    public boolean isElementDisplayed(String element) throws Exception{

        try{
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            log.info(String.format("Esperar elemento: ", element));
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            isDisplayed = wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement)).isDisplayed();
        }catch (NoSuchElementException | TimeoutException e){
            isDisplayed = false;
            log.info(e);
        }
        log.info(String.format(" el elemento visible es: ", element, isDisplayed));
        System.out.println(String.format(" el elemento visible es: %s", element, isDisplayed));
        return isDisplayed;
    }

    public void fileUpload(String element) throws Exception{
        String attribute = readProperties("FilesUploads");
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebElement fileInput = driver.findElement(SeleniumElement);
        fileInput.sendKeys(attribute);
        log.info(String.format("se sube el archivo Correctamente"));
    }

    public void searchImage(String element) throws FindFailed, IOException {
        Screen screen = new Screen();
        File Folder = new File(readProperties("PathStorage"));
        File[] files = Folder.listFiles();
        List<String> SikulyImage = new ArrayList<String>();
        for (int i = 0; i < files.length; i++){
            SikulyImage.add(removeExtension(files[i].getName()));
        }
        for (String i: SikulyImage) {
            System.out.println(i);
            if(i.contains(element)){
                Pattern Image = new Pattern(Folder + "/" + i);
                System.out.println(Image);
                screen.wait(Image,10);
                screen.click(Image);
                break;
            }
        }
        log.info("Entro Correctamente al Reguistro");
    }

    public void addAttributeSelect(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("var e = document.getElementsByTagName('select');" +
                "console.log(e);" +
                "for (var i = 0; i < e.length; i++) {" +
                "var n = i + 1;" +
                "console.log(e[i]);" +
                "e[i].setAttribute('id', n);" +
                "}");
        log.info("se realizo la adicion");
    }

    public void selectvsGroupMasive(String select, String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(select);
        List<WebElement> selected = driver.findElements(SeleniumElement);
        for (int i = 0; i < selected.size(); i++) {
            System.out.println("numero de elementos " + selected.get(i).getAttribute("id"));
            String nombre_id = selected.get(i).getAttribute("id");
            Select opt = new Select(driver.findElement(By.id(nombre_id)));
            driver.findElements(By.tagName("optgroup"));
            opt.selectByVisibleText(element);
        }
    }



}
