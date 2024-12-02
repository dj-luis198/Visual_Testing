package examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class WebVisualComparatorTest {
    private WebDriver driver;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> pref = new HashMap<>();
        pref.put("download.prompt_for_download", false);
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--start-fullscreen");
        options.addArguments("--no-proxy-server");
        options.addArguments("--ignore-certificate-errors");
        options.setExperimentalOption("prefs", pref);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void initVisualComparator() throws Exception {
        VisualComparator visualComparator = new VisualComparator(driver);
        driver.get("https://demoqa.com/links");
        visualComparator.savePage();
    }

    @Test
    public void compareWithVisualComparator() throws Exception {
        VisualComparator visualComparator = new VisualComparator(driver);
        driver.get("https://demoqa.com/links");
        visualComparator.comparePage();
    }

    @Test
    public void modifyBaseWithVisualComparator() throws Exception {
        VisualComparator visualComparator = new VisualComparator(driver);
        driver.get("https://demoqa.com/links");
        visualComparator.modifyBase();
    }

    @Test
    public void testCssChanges() throws Exception {
        driver.get("https://demoqa.com/buttons");
        VisualComparator visualComparator = new VisualComparator(driver);
        visualComparator.modifyBase();
        WebElement element =driver.findElement(By.xpath("(//button[normalize-space()='Click Me'])[1]"));
        WebElement element2 =driver.findElement(By.cssSelector("#rightClickBtn"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].classList.add('MYCLASS');", element);
        js.executeScript("arguments[0].parentNode.removeChild(arguments[0]);", element2);
        visualComparator.comparePage();
    }
}
