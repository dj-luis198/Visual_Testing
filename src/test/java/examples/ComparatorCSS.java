package examples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.util.*;


public class ComparatorCSS {
    private ChromeDriver driver;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> pref = new HashMap<String, Object>();
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
    public void testCssChanges() {
        // Navegar a la página
        driver.get("https://demoqa.com/buttons");

        // Obtener el CSS inicial
        String initialCss = getCss(driver);

        // Realizar acciones

        WebElement boton = driver.findElement(By.xpath("(//button[normalize-space()='Click Me'])[1]"));
        String js = "arguments[0].style.backgroundColor = 'red';";
        ((JavascriptExecutor) driver).executeScript(js, boton);

        WebElement boton2 = driver.findElement(By.xpath("//button[@id='doubleClickBtn']"));
        String js2 = "arguments[0].style.backgroundColor = 'blue';";
        ((JavascriptExecutor) driver).executeScript(js2, boton2);

        try {
            Thread.sleep(10000); // Aumentar el tiempo de espera a 10 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Obtener el CSS final
        String finalCss = getCss(driver);

        // Comparar los CSS
        List<String> differences = compareCss(initialCss, finalCss,driver);

        // Mostrar los resultados
        if (!differences.isEmpty()) {
            System.out.println("El CSS de la página ha cambiado");
            System.out.println("Diferencias:");
            for (String difference : differences) {
                System.out.println(difference);
            }
        } else {
            System.out.println("El CSS de la página no ha cambiado");
        }
    }

    private String getCss(WebDriver driver) {
        // Extraer los estilos CSS de la página
        String html = driver.getPageSource();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("*[style]");
        StringBuilder css = new StringBuilder();

        for (Element element : elements) {
            css.append(element.attr("style"));
        }

        return css.toString();
    }

    private List<String> compareCss(String initialCss, String finalCss, WebDriver driver) {
        // Comparar los estilos CSS
        List<String> differences = new ArrayList<>();
        String[] initialStyles = initialCss.split(";");
        String[] finalStyles = finalCss.split(";");

        for (int i = 0; i < finalStyles.length; i++) {
            String finalStyle = finalStyles[i].trim();
            boolean found = false;
            for (int j = 0; j < initialStyles.length; j++) {
                String initialStyle = initialStyles[j].trim();
                if (finalStyle.equals(initialStyle)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Buscar el elemento asociado al estilo
                WebElement element = getElementForStyle(driver, finalStyle);
                if (element != null) {
                    String selector = getElementSelector(element);
                    differences.add("Estilo CSS nuevo: " + finalStyle + " en elemento " + selector);
                } else {
                    differences.add("Estilo CSS nuevo: " + finalStyle);
                }
            }
        }

        for (int i = 0; i < initialStyles.length; i++) {
            String initialStyle = initialStyles[i].trim();
            boolean found = false;
            for (int j = 0; j < finalStyles.length; j++) {
                String finalStyle = finalStyles[j].trim();
                if (finalStyle.equals(initialStyle)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Buscar el elemento asociado al estilo
                WebElement element = getElementForStyle(driver, initialStyle);
                if (element != null) {
                    String selector = getElementSelector(element);
                    differences.add("Estilo CSS eliminado: " + initialStyle + " en elemento " + selector);
                } else {
                    differences.add("Estilo CSS eliminado: " + initialStyle);
                }
            }
        }

        return differences;
    }

    private WebElement getElementForStyle(WebDriver driver, String style) {
        // Buscar el elemento que tiene el estilo
        List<WebElement> elements = driver.findElements(By.cssSelector("*[style*='" + style + "']"));
        if (!elements.isEmpty()) {
            return elements.get(0);
        }
        return null;
    }

    private String getElementSelector(WebElement element) {
        // Obtener el selector del elemento
        String selector = "";
        if (element.getAttribute("id") != null) {
            selector += "#" + element.getAttribute("id");
        }
        if (element.getAttribute("class") != null) {
            selector += "." + element.getAttribute("class").replace(" ", ".");
        }
        selector += element.getTagName();
        return selector;
    }
}