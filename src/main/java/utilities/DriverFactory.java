package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
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
        String hubUrl = System.getenv("HUB_URL"); // 🚀 Detect if HUB_URL is provided (like in docker-compose)

        try {
            if (hubUrl != null && !hubUrl.isEmpty()) {
                // --- Running tests remotely (Docker/Selenium Grid) ---
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName(browserName);
                ChromeOptions options = new ChromeOptions();
                if (isHeadless) {
                    options.addArguments("--headless=new", "--window-size=1920,1080");
                }
                capabilities.merge(options);

                driver.set(new RemoteWebDriver(new URL(hubUrl), capabilities));
            } else {
                // --- Running tests locally ---
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
                            boolean isCI = System.getenv("CI") != null;
                            if (isCI) {
                                String tmpProfilePath = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + UUID.randomUUID();
                                options.addArguments("--user-data-dir=" + tmpProfilePath);
                            } else {
                                options.addArguments("--user-data-dir=C:/Users/R M/ChromeProfile");
                            }

                            options.addArguments(
                                    "--disable-blink-features=AutomationControlled",
                                    "--no-sandbox",
                                    "--disable-dev-shm-usage",
                                    "--headless=new",
                                    "--disable-gpu",
                                    "--window-size=1920,1080"
                            );

                            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                            options.setExperimentalOption("useAutomationExtension", false);

                            options.setExperimentalOption("prefs", java.util.Map.of(
                                    "credentials_enable_service", false,
                                    "profile.password_manager_enabled", false
                            ));
                        }
                        driver.set(new ChromeDriver(options));
                        break;
                }
            }

            int implicitWaitTime = Integer.parseInt(PropertyUtils.get("implicitWait"));
            driver.get().manage().window().maximize();
            driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
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
