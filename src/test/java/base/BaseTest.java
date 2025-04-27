package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import utilities.DriverFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.PropertyUtils;
import utilities.ExtentManager;
import utilities.Log;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class BaseTest {

	protected WebDriver driver;
	protected static ExtentReports extent;
	protected static ExtentTest test;
	private static final Logger logger = LogManager.getLogger(BaseTest.class);

	@BeforeSuite
	public void beforeSuite() {
		extent = ExtentManager.getExtentReports();
	}

	@BeforeMethod
	public void setUp() {
		driver = DriverFactory.getDriver();
	}


	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) {
		DriverFactory.quitDriver();
	}

	@AfterSuite
	public void afterSuite() {
		if (extent != null) {
			extent.flush();
		}
	}
}
