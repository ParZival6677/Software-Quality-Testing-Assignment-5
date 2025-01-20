package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class AmazonTests {
    WebDriver driver;
    WebDriverWait wait;
    Logger log = Logger.getLogger(AmazonTests.class);
    ExtentReports extentReports;
    ExtentTest test;


    @BeforeClass
    public void setup() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/reports/extentReport.html");
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        PropertyConfigurator.configure(getClass().getClassLoader().getResource("log4j.properties"));
        log.info("Setting up the test environment...");

        driver = new EdgeDriver();
        log.info("Edge driver initialized.");

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        HashMap<String, Object> edgeOptions = new HashMap<>();
        edgeOptions.put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36");
        ((EdgeDriver) driver).executeCdpCommand("Network.setUserAgentOverride", edgeOptions);

        log.info("User-Agent set.");
    }

    @Test
    public void testProductSearchAndResultsPage() {
        test = extentReports.createTest("testProductSearchAndResultsPage");

        log.info("Starting test: testProductSearchAndResultsPage");
        driver.get("https://www.amazon.com");
        log.info("Navigated to Amazon homepage.");

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        log.info("Search box found, entering 'Headphones'...");
        searchBox.sendKeys("Headphones");
        searchBox.submit();
        log.info("Search submitted.");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.s-main-slot")));
        log.info("Search results loaded.");

        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.contains("Headphones"), "Search results page title contains 'Headphones'");
        log.info("Page title verified.");

        takeScreenshot();
    }

    @Test
    public void testFilterByDeals() {
        test = extentReports.createTest("testFilterByDeals");
        log.info("Starting test: testFilterByDeals");

        driver.get("https://www.amazon.com");
        log.info("Navigated to Amazon homepage.");

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.sendKeys("laptop");
        searchBox.submit();
        log.info("Search for 'laptop' submitted.");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.s-main-slot")));

        WebElement todaysDealsLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("p_n_deal_type/23566064011")));
        todaysDealsLink.click();
        log.info("Clicked on 'Today's Deals'.");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.s-main-slot")));

        List<WebElement> todaysDealsResults = driver.findElements(By.cssSelector("div.s-main-slot div.s-result-item"));
        Assert.assertTrue(todaysDealsResults.size() > 0, "'Today's Deals' products not found.");

        WebElement allDiscountsLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("p_n_deal_type/23566065011")));
        allDiscountsLink.click();
        log.info("Clicked on 'All Discounts'.");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.s-main-slot")));

        List<WebElement> allDiscountsResults = driver.findElements(By.cssSelector("div.s-main-slot div.s-result-item"));
        Assert.assertTrue(allDiscountsResults.size() > 0, "'All Discounts' products not found.");

        log.info("Both filters ('Today's Deals' and 'All Discounts') verified successfully.");

        takeScreenshot();
    }

    
    @Test
    public void testSignInButtonPresence() {
        test = extentReports.createTest("testSignInButtonPresence");

        log.info("Starting test: testSignInButtonPresence");
        driver.get("https://www.amazon.com");
        log.info("Navigated to Amazon homepage.");

        WebElement signInButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList")));
        Assert.assertTrue(signInButton.isDisplayed(), "'Sign-In' button is present on the homepage.");
        log.info("'Sign-In' button presence verified.");

        takeScreenshot();
    }

    @Test
    public void testFooterLinksPresence() {
        test = extentReports.createTest("testFooterLinksPresence");

        log.info("Starting test: testFooterLinksPresence");
        driver.get("https://www.amazon.com");
        log.info("Navigated to Amazon homepage.");

        WebElement footer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("navFooter")));
        log.info("Footer section found.");

        WebElement aboutAmazonLink = footer.findElement(By.linkText("About Amazon"));
        WebElement careersLink = footer.findElement(By.linkText("Careers"));
        WebElement helpLink = footer.findElement(By.linkText("Help"));

        Assert.assertTrue(aboutAmazonLink.isDisplayed(), "'About Amazon' link is present in the footer.");
        Assert.assertTrue(careersLink.isDisplayed(), "'Careers' link is present in the footer.");
        Assert.assertTrue(helpLink.isDisplayed(), "'Help' link is present in the footer.");
        log.info("Footer links presence verified.");
        
        takeScreenshot();
    }
    

    @Test
    public void testSearchSuggestions() {
        test = extentReports.createTest("testSearchSuggestions");

        log.info("Starting test: testSearchSuggestions");
        driver.get("https://www.amazon.com");
        log.info("Navigated to Amazon homepage.");

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        log.info("Search box found, entering 'phone'...");
        searchBox.sendKeys("phone");

        WebElement suggestionsList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.autocomplete-results-container")));
        log.info("Suggestions list displayed.");

        List<WebElement> suggestions = suggestionsList.findElements(By.cssSelector("div.s-suggestion"));
        Assert.assertTrue(suggestions.size() > 0, "Search suggestions are displayed.");
        log.info("Search suggestions verified.");

        takeScreenshot();
    }

    @AfterClass
    public void teardown() {
        try {
            log.info("Starting teardown...");
            if (driver != null) {
                driver.quit();
                log.info("Driver closed.");
            }
        } catch (Exception e) {
            log.error("An error occurred during teardown: ", e);
        }
        extentReports.flush();
        log.info("Extent report generated.");
    }

    public void takeScreenshot() {
        try {
            String screenshotPath = "C:\\Users\\Dias\\Desktop\\ass3\\demo\\src\\screenshots\\" + System.currentTimeMillis() + ".png";
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.createDirectories(Paths.get("screenshots"));
            Files.copy(screenshotFile.toPath(), Paths.get(screenshotPath));
            test.addScreenCaptureFromPath(screenshotPath);
            log.info("Screenshot captured: " + screenshotPath);
        } catch (IOException e) {
            log.error("Failed to capture screenshot: " + e.getMessage());
        }
    }
}
