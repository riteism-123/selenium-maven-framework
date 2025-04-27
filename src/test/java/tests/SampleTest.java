package tests;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utilities.DriverFactory;
import utilities.ExtentLogger;
import utilities.ExtentManager;
import utilities.Log;
import listeners.TestListener;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import base.BaseTest;

@Listeners(TestListener.class)
public class SampleTest extends BaseTest {

	WebDriver driver;
	ExtentReports extent;
	ExtentTest test;

	@Test(groups = {"regression"})
	public void validateWrongTitle() {

		Log.info("Starting test: testFailureExample"); // <-- Log here
		DriverFactory.getDriver().get("https://www.google.com/invalidpage");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		Log.info("Navigated to Google Invalid homepage."); 
		String actualTitle = driver.getTitle();
		Log.info("Page Title is: " + actualTitle);
		Assert.assertTrue(actualTitle.contains("Google"), "Title does not contain expected text: Google");

	}

	@Test
	public void testGoogleHomePage() {

		Log.info("Starting test: testGoogleHomePage"); // <-- Log here
		driver.get("https://www.google.com");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		String actualTitle = driver.getTitle();
		Log.info("Page Title is: " + actualTitle);
		//Assert.assertEquals(actualTitle, "Google");


		Assert.assertTrue(actualTitle.contains("Google"), "Title does not contain expected text: Google");
		Log.info("Title validation passed successfully.");
	}

	@Test(groups = {"smoke", "sanity"})
	public void validateHomePageTitle() {
		driver = DriverFactory.getDriver();
		driver.get("https://www.google.com");
		ExtentLogger.info("Navigated to Google homepage");
		Log.info("Navigated to Google homepage");

		String actualTitle = driver.getTitle();
		ExtentLogger.info("Captured page title: " + actualTitle);
		Log.info("Captured page title: " + actualTitle);

		Assert.assertTrue(actualTitle.contains("Google"), "Title does not contain expected text: Google");
		ExtentLogger.pass("Title validation passed successfully.");
		Log.info("Title validation passed successfully.");
	}
}