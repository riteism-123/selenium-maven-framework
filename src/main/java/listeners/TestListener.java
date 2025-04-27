package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import utilities.DriverFactory;
import utilities.ExtentLogger;
import utilities.ExtentManager;
import utilities.Log;
import utilities.ScreenshotUtils;


public class TestListener implements ITestListener {

    ExtentReports extent = ExtentManager.getExtentReports();
    ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
        ExtentLogger.setExtentTest(extentTest);
        Log.info("Starting Test: " + result.getMethod().getMethodName());
  
        
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        String path = ScreenshotUtils.takeScreenshot(driver, result.getMethod().getMethodName());
        test.get().pass("Test Passed").addScreenCaptureFromPath(path);
        Log.info("Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        String path = ScreenshotUtils.takeScreenshot(driver, result.getMethod().getMethodName());
        test.get().fail(result.getThrowable()).addScreenCaptureFromPath(path);
        Log.error("Test Failed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        Log.info("Finished executing tests.");
    }
}
