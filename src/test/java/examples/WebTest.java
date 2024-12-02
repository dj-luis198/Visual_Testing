package examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebTest {
    private ChromeDriver driver;

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
    public void compareCssTest() throws IOException {
        driver.get("https://demoqa.com/buttons");
        ComparatorCssExt comparator = new ComparatorCssExt(driver);
        List <String>baseCss= comparator.getCssExternal();
        comparator.saveCssExternalToFile();
    }

    @Test
    public void testCssComparatorFromFile() throws IOException {
        driver.get("https://demoqa.com/buttons");
        ComparatorCssExt comparator = new ComparatorCssExt(driver);
        List<String> discrepancies = comparator.compareCssFromFile();
        if (!discrepancies.isEmpty()) {
            System.out.println("Discrepancias:");
            for (String discrepancy : discrepancies) {
                System.out.println(discrepancy);
            }
        } else {
            System.out.println("No hay Discrepancias");
        }
    }

    @Test
    public void testCssChanges() throws IOException {
        driver.get("https://demoqa.com/buttons");
        ComparatorCssExt comparator = new ComparatorCssExt(driver);
        WebElement link = driver.findElement(By.cssSelector("link[href='/main.css']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].remove()", link);
        List<String> discrepancies = comparator.compareCssFromFile();
        if (!discrepancies.isEmpty()) {
            System.out.println("Discrepancias:");
            for (String discrepancy : discrepancies) {
                System.out.println(discrepancy);
            }
        } else {
            System.out.println("No hay Discrepancias");
        }
    }

    @Test
    public void baseHtmlTest() throws IOException {
        driver.get("https://demoqa.com/buttons");
        ComparatorHTML comparador = new ComparatorHTML(driver);
        comparador.saveHtml();
    }

    @Test
    public void compareHtmlTest() throws IOException {
        driver.get("https://demoqa.com/buttons");
        ComparatorHTML comparador = new ComparatorHTML(driver);
        List<String> discrepancies = comparador.compareHtmlFromFile();
        if (!discrepancies.isEmpty()) {
            System.out.println("Discrepancias:");
            for (String discrepancy : discrepancies) {
                System.out.println(discrepancy);
            }
        } else {
            System.out.println("No hay Discrepancias");
        }
    }

    @Test
    public void compareHtmlChangesTest() throws IOException {
        driver.get("https://demoqa.com/buttons");
        ComparatorHTML comparador = new ComparatorHTML(driver);
        WebElement boton = driver.findElement(By.xpath("(//button[normalize-space()='Click Me'])[1]"));
        boton.click();
        String js = "arguments[0].style.backgroundColor = 'red';";
        ((JavascriptExecutor) driver).executeScript(js, boton);
        List<String> discrepancies = comparador.compareHtmlFromFile();
        if (!discrepancies.isEmpty()) {
            System.out.println("Discrepancias:");
            for (String discrepancy : discrepancies) {
                System.out.println(discrepancy);
            }
        } else {
            System.out.println("No hay Discrepancias");
        }
    }

}
