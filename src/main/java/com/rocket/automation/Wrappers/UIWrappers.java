package com.rocket.automation.Wrappers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.rocket.automation.Utils.PropertyLoader;
import com.rocket.automation.Utils.Constant;
import io.github.bonigarcia.wdm.WebDriverManager;



public class UIWrappers {

    public String temp;

    public UIWrappers() {
        propertyLoader();
    }

    protected PropertyLoader propLoader;

    public WebDriver driver;
    public static ThreadLocal<WebDriver> thread_driver = new ThreadLocal<WebDriver>();

    /**
     * Function Name : LaunchBrowser with user provided URL Description : To Launch
     * Browser either Chrome, Firefox, etc.,
     */
    @Step("Open browser and maximize the window")
    public WebDriver launchBrowser() {
        String BrowserType = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
                .getParameter("browserType");
        if (BrowserType == null ) {BrowserType = "chrome";}
        if (BrowserType.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (BrowserType.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else if (BrowserType.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
        thread_driver.set(driver);
        windowMaximize(driver);
        return getDriver();

    }

//	public WebDriver getDriver1() {
//
//		return driver;
//	}

    public static synchronized WebDriver getDriver() {
        return thread_driver.get();
    }

    @Step("Maximized the window")
    public void windowMaximize(WebDriver driver) {
        driver.manage().window().maximize();
    }

    public void propertyLoader() {
        propLoader = new PropertyLoader();
        propLoader.loadProperty();
    }

    /**
     * Function Name : takeScreenShot Description : To take screenshot of the
     * current webpage
     */
    public void takeScreenShot(String className, WebDriver driver) {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshotFile, new File(Constant.SCREENSHOTS + className + ".jpg"));
            // log code
        } catch (IOException e) {
            System.out.println("Screenshot cannot be taken, IOException is thrown" + e.getMessage());
        }
    }

    /**
     * Method Name : DropdownSelectbyText Description : To select dropdown by Text
     */
    public boolean isElementPresent(WebElement element) {
        try {
            element.isDisplayed();

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean isElementNotPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by);

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean isNotElementPresent(WebElement element) {
        try {
            Assert.assertFalse(element.isDisplayed());

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean isElementEnabled(WebElement element) {
        if (element.isEnabled())
            return true;
        else
            return false;
    }

    public boolean isElementSelected(WebElement element) {
        try {
            element.isSelected();

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Method Name : isElementsListPresent Description : It verifies the list of
     * elements present
     */
    public boolean isElementsListPresent(List<WebElement> list) {

        if (list.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * Method Name : acceptAlert Description : To accept alert
     */
    public void acceptAlert(WebDriver driver) throws Exception {
        try {
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            throw new Exception("No Alert Present");
        }
    }

    // Wait for Angular Load
    public static void waitForAngularLoad(WebDriver webDriver) {
        WebDriver jsWaitDriver = webDriver;

        WebDriverWait wait = new WebDriverWait(jsWaitDriver, 10);
        // Get Angular is Ready
        ExpectedCondition<Boolean> expectation = driver -> ((JavascriptExecutor) driver)
                .executeAsyncScript("var callback = arguments[arguments.length - 1];"
                        + "if (document.readyState !== 'complete') {" + "  callback('document not ready');" + "} else {"
                        + "  try {" + "    var testabilities = window.getAllAngularTestabilities();"
                        + "    var count = testabilities.length;" + "    var decrement = function() {"
                        + "      count--;" + "      if (count === 0) {" + "        callback('complete');" + "      }"
                        + "    };" + "    testabilities.forEach(function(testability) {"
                        + "      testability.whenStable(decrement);" + "    });" + "  } catch (err) {"
                        + "    callback(err.message);" + "  }" + "}")
                .toString().equals("complete");

        try {
            wait.until(expectation);
        } catch (Exception e) {
            new Exception("Timeout waiting for Page Load Request to complete.");
        }
    }

    public String getElementText(WebElement webElement) {
        String text;
        text = webElement.getText().trim();
        return text;
    }

    /**
     * Method Name : storeTemporaryText Description : Storing run time information
     * into temp variable
     */
    public void storeTemporaryText(String text) {
        temp = text;
    }

    /**
     * Method Name : getTemporaryText Description : Getting run time information
     * from temp variable
     */
    public String getTemporaryText() {
        return temp;
    }

    /**
     * Method Name : Close Browser Description : To close browser
     */
    @Step("Closing the driver {0}")
    public void closeBrowser(WebDriver driver) {
        try {
            driver.quit();
            // LoggerUtil.Log.info("Launched Browser closed successfully");
        } catch (Exception e) {
            // LoggerUtil.Log.info("Browser cant be closed, exception throws");
        }
    }
    public String getNUMfromString(String text) {
        String CharSpecialChar;
        CharSpecialChar = text.replaceAll("[^0-9.]", "");
        return CharSpecialChar;
    }
}
