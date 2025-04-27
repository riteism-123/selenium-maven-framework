package utilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentLogger {

    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static void setExtentTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static ExtentTest getExtentTest() {
        return extentTest.get();
    }

    public static void info(String message) {
    	getExtentTest().info(MarkupHelper.createLabel(message, ExtentColor.BLUE));
    }

    public static void pass(String message) {
    	 getExtentTest().pass(MarkupHelper.createLabel(message, ExtentColor.GREEN));
    }

    public static void fail(String message) {
    	getExtentTest().fail(MarkupHelper.createLabel(message, ExtentColor.RED));
    }
}
