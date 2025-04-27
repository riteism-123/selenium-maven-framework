package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.UUID;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    private static void initializeDriver() {
        String browserName = System.getProperty("browser", PropertyUtils.get("browser")).toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", PropertyUtils.get("headless")));

        switch (browserName) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("--headless");
                    firefoxOptions.addArguments("--window-size=1920,1080");
                }
                driver.set(new FirefoxDriver(firefoxOptions));
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isHeadless) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--window-size=1920,1080");
                }
                driver.set(new EdgeDriver(edgeOptions));
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (isHeadless) {
                	// Required for CI (random temp profile)
                    String tmpProfilePath = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + UUID.randomUUID();
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
                    options.addArguments("--window-size=1920,1080");

                    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                    options.setExperimentalOption("useAutomationExtension", false);

                    // Disable password save prompts
                    options.setExperimentalOption("prefs", java.util.Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false
                    ));
                }
                driver.set(new ChromeDriver(options));
                break;
        }

        int implicitWaitTime = Integer.parseInt(PropertyUtils.get("implicitWait"));
        driver.get().manage().window().maximize();
        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                driver.get().quit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                driver.remove();
            }
        }
    }
}
