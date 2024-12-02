package examples;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.List;

public class VisualComparator {
    WebDriver driver;
    ComparatorHTML html;
    ComparatorCssExt css;

    public VisualComparator(WebDriver driver) {
        this.driver = driver;
    }

    public void savePage() throws IOException {
        html = new ComparatorHTML(driver);
        css = new ComparatorCssExt(driver);
        html.saveHtml();
        css.saveCssExternalToFile();
    }

    public void comparePage() throws IOException {
        html = new ComparatorHTML(driver);
        css = new ComparatorCssExt(driver);
        List <String>resultHtml= html.compareHtmlFromFile();
        List <String> resultCss = css.compareCssFromFile();
        System.out.println("HTML results: ");
        printResults(resultHtml);
        System.out.println("CSS results: ");
        printResults(resultCss);
    }

    public void modifyBase() throws IOException {
        html = new ComparatorHTML(driver);
        css = new ComparatorCssExt(driver);
        html.overwriteBaseHtml();
        css.overwriteBaseCss();
    }

    private void printResults(List<String> results) {
        for (String result : results) {
            System.out.println(result);
        }
    }
}
