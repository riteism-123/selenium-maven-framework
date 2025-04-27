package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import utilities.DriverFactory;

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
	@Parameters("browser")
	public void setUp(@Optional("chrome") String browserName) {
		Log.info("Starting test setup");

		if (browserName.equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
 
            // Required for CI (random temp profile)
            String tmpProfilePath = System.getProperty("java.io.tmpdir") + "/chrome-profil-" + UUID.randomUUID();
            options.addArguments("--user-data-dir=" + tmpProfilePath);
            
            boolean isCI = System.getenv("CI") != null;

            if (isCI) {
                // Use temp profile (as above)
            } else {
                // Use local user-data-dir
                options.addArguments("--user-data-dir=C:/Users/R M/ChromeProfile");
            }   

            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless=new"); // headless mode for CI
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");           
         
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            // Disable password save prompts
            options.setExperimentalOption("prefs", java.util.Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
            ));

            driver = new ChromeDriver(options);
            
		} else if (browserName.equalsIgnoreCase("edge")) {
			WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");

            // Fix: unique user-data-dir for Edge too
            String uniqueUserDataDir = "/tmp/edge-user-data-" + System.currentTimeMillis();
            options.addArguments("--user-data-dir=" + uniqueUserDataDir);

            driver = new EdgeDriver(options);


		} else if (browserName.equalsIgnoreCase("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions options = new FirefoxOptions();
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--headless=new");
			driver = new FirefoxDriver(options);

		} else {
			throw new RuntimeException("Unsupported browser: " + browserName);
		}

		driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		//driver.get(PropertyUtils.getProperty("url"));

		//Log.info("Browser launched and navigated to URL: " + ConfigReader.getProperty("url"));
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
