import WebAutomationBase.helper.ElementHelper;
import WebAutomationBase.helper.StoreHelper;
import WebAutomationBase.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.util.List;
import java.util.Random;


public class StepImp extends BaseTest {

    private static final int DEFAULT_MAX_ITERATION_COUNT = 75;
    private static final int DEFAULT_MILLISECOND_WAIT_AMOUNT = 200;
    private static Log4jLoggerAdapter logger = (Log4jLoggerAdapter) LoggerFactory
            .getLogger(StepImp.class);
    private Actions actions = new Actions(driver);

    public StepImp() {

        PropertyConfigurator
                .configure(StepImp.class.getClassLoader().getResource("log4j.properties"));
    }

    private WebElement findElement(String key) {
        try {
            ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
            By infoParam = ElementHelper.getElementInfoToBy(elementInfo);

            WebDriverWait webDriverWait = new WebDriverWait(driver, 60);
            WebElement webElement = webDriverWait
                    .until(ExpectedConditions.presenceOfElementLocated(infoParam));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                    webElement);
            return webElement;
        } catch (Exception e) {
            return null;
        }
    }

    public static int randomNumber(int start, int end) {
        Random rn = new Random();
        int randomNumber = rn.nextInt(end - 1) + start;
        return randomNumber;
    }

    public List<WebElement> findElementsByKey(String key) {

        return driver.findElements(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key)));
    }

    @Step({"Wait for element to load with <key> and click",
            "Elementin yüklenmesini bekle ve tıkla <key>"})
    public WebElement getElementWithKeyIfExists(String key) {
        WebElement webElement;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            try {
                webElement = findElement(key);
                if (webElement != null) {
                    logger.info(key + " elementi bulundu.");
                    actions.moveToElement(findElement(key));
                    actions.click();
                    actions.build().perform();
                    logger.info(key + " elementine focus ile tıklandı.");
                    return webElement;
                }
            } catch (WebDriverException e) {
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assert.fail("Element: '" + key + "' doesn't exist.");
        return null;
    }

    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            logger.info(milliseconds + " milisaniye bekleniyor.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"Write value <text> to element <key>",
            "<text> textini <key> elemente yaz"})
    public void sendKeys(String text, String key) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazıldı.");
        }
    }

    @Step({"Check if element <key> contains text <expectedText>",
            "<key> elementi <text> değerini içeriyor mu kontrol et"})
    public void checkElementContainsText(String key, String expectedText) {
        Boolean containsText = findElement(key).getText().contains(expectedText);
        Assert.assertTrue("Expected text is not contained", containsText);

    }

    @Step({"Wait for element to load with key <key>",
            "Elementin yüklenmesini bekle <key>"})
    public WebElement waitElementloading(String key) {
        WebElement webElement;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            try {
                webElement = findElement(key);
                logger.info(key + " elementi bulundu.");
                return webElement;
            } catch (WebDriverException e) {
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assert.fail("Element: '" + key + "' doesn't exist.");
        return null;
    }

    @Step("Erkek tabına <tab> tıklanır, butikler <boutique> gelmiyorsa log basılır")
    public void tabClick(String tab, String boutique) throws InterruptedException {

            findElement(tab).click();
            List<WebElement> imageList = findElementsByKey(boutique);

            if (imageList.size() > 0) {
                logger.info("Butikler yüklendi!");
            } else {
                logger.info("Butikler yüklenemedi!");
            }
        }

    @Step("Erkek tabına tıklandıktan sonra rastgele bir butik <boyBoutique> seçilir")
    public void randomClickBoutique(String boyBoutique) {

        List<WebElement> boutiqueList = findElementsByKey(boyBoutique);
        int randomBoutique = randomNumber(0, boutiqueList.size());

        WebElement getBoutique = boutiqueList.get(randomBoutique);

        getBoutique.click();

    }

    @Step("Rastgele bir ürün görseline <image> tıklanır")
    public void randomClickImage(String image) {

        List<WebElement> imageList = findElementsByKey(image);
        int randomImageNumber = randomNumber(0, imageList.size());

        WebElement getImage = imageList.get(randomImageNumber);

        getImage.click();

    }

}


