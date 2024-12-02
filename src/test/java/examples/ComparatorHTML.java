package examples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ComparatorHTML {
    private final WebDriver driver;

    public ComparatorHTML(WebDriver driver) {
        this.driver = driver;
    }

    public List<String> compareHtmlFromFile() throws IOException {
        String currentPage = driver.getTitle();
        String url = driver.getCurrentUrl();
        if (currentPage == null || url == null) {
            throw new IllegalArgumentException("Current page or URL is null");
        }
        String oldFileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_base.html";
        String newFileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_new.html";
        if(!new File(oldFileName).exists()) {
            throw new FileNotFoundException("You need to create a base file. Use the savePage() command.");
        }
        saveHtml();
        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);
        if (!oldFile.exists()) {
            throw new FileNotFoundException("HTML file does not exist");
        }
        if (!newFile.exists()) {
            throw new FileNotFoundException("The new HTML file does not exist");
        }
        String oldHtml = readFile(oldFile);
        String newHtml = readFile(newFile);
        return compareHtml(oldHtml, newHtml);
    }

    private String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

    public void saveHtml() {
        String currentPage = driver.getTitle();
        String url = driver.getCurrentUrl();
        if (currentPage == null || url == null) {
            throw new IllegalArgumentException("Current page or URL is null");
        }
        String fileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_base.html";
        File file = new File(fileName);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs(); // crea las carpetas necesarias si no existen
        }
        if (file.exists()) {
            fileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_new.html";
            file = new File(fileName);
            dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs(); // crea las carpetas necesarias si no existen
            }
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            file.createNewFile();
            fileWriter.write(getHtml(driver));
            System.out.println("File created successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error creating file " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void overwriteBaseHtml() {
        String currentPage = driver.getTitle();
        String url = driver.getCurrentUrl();
        if (currentPage == null || url == null) {
            throw new IllegalArgumentException("Current page or URL is null");
        }
        String fileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_base.html";
        File file = new File(fileName);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs(); // crea las carpetas necesarias si no existen
        }
        if (file.exists()) {
            file.delete(); // elimina el archivo base si ya existe
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            file.createNewFile();
            fileWriter.write(getHtml(driver));
            System.out.println("Base HTML file overwritten successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error overwriting base file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getHtml(WebDriver driver) {
        return driver.getPageSource();
    }

    public List<String> compareHtml(String initialHtml, String finalHtml) {
        Document docInitial = Jsoup.parse(initialHtml);
        Document docFinal = Jsoup.parse(finalHtml);
        List<String> differences = new ArrayList<>();
        compareElements(docInitial.body(), docFinal.body(), differences);
        return differences;
    }

    private void compareElements(Element initialElement, Element finalElement, List<String> differences) {
        if (!initialElement.tagName().equals(finalElement.tagName())) {
            differences.add("\n --------- Element changed: " + initialElement + " -> " + finalElement);
        } else if (!initialElement.attr("class").equals(finalElement.attr("class"))) {
            differences.add("\n --------- Class changed: " + initialElement + " -> " + finalElement);
        } else if (!initialElement.attr("style").equals(finalElement.attr("style"))) {
            differences.add("\n --------- Style changed: " + initialElement + " -> " + finalElement);
        } else if (!initialElement.text().equals(finalElement.text())) {
            differences.add("\n --------- Text changed: " + initialElement + " -> " + finalElement);
        }
        Elements initialChildren = initialElement.children();
        Elements finalChildren = finalElement.children();
        int initialIndex = 0;
        int finalIndex = 0;
        while (initialIndex < initialChildren.size() && finalIndex < finalChildren.size()) {
            Element initialChild = initialChildren.get(initialIndex);
            Element finalChild = finalChildren.get(finalIndex);
            compareElements(initialChild, finalChild, differences);
            initialIndex++;
            finalIndex++;
        }

        while (initialIndex < initialChildren.size()) {
            Element initialChild = initialChildren.get(initialIndex);
            differences.add("Deleted element: " + initialChild);
            initialIndex++;
        }

        while (finalIndex < finalChildren.size()) {
            Element finalChild = finalChildren.get(finalIndex);
            differences.add("New element: " + finalChild);
            finalIndex++;
        }
    }

    private String getNamePage(String url) {
        String[] urlParts = url.split("/");
        String pageName = urlParts[urlParts.length - 1];
        if (pageName.contains("?")) {
            pageName = pageName.split("\\?")[0];
        }
        return pageName;
    }
}