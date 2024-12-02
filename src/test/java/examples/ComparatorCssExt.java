package examples;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComparatorCssExt {
    private final WebDriver driver;

    public ComparatorCssExt(WebDriver driver) {
        this.driver = driver;
    }

    public List<String> compareCssFromFile() throws IOException {
        String currentPage = driver.getTitle();
        String url = driver.getCurrentUrl();
        saveCssExternalToFile();
        String oldFileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_base.css";
        String newFileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_new.css";
        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);
        if (!oldFile.exists()) {
            throw new FileNotFoundException("You need to create a base file. Use the savePage() command.");
        }
        List<String> oldCss = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(oldFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                oldCss.add(line);
            }
        }
        List<String> newCss = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(newFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                newCss.add(line);
            }
        }
        return compareCss(oldCss, newCss);
    }


    public void saveCssExternalToFile() throws IOException {
        List <String> cssExternal = getCssExternal();
        String currentPage = driver.getTitle();
        String url = driver.getCurrentUrl();
        String fileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_base.css";
        String filePath = fileName;
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs(); // crea las carpetas necesarias si no existen
        }
        if (file.exists()) {
            fileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_new.css";
            filePath = fileName;
            file = new File(filePath);
            dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs(); // crea las carpetas necesarias si no existen
            }
        }
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            for (String css : cssExternal) {
                fileWriter.write(css + "\n");
            }
            fileWriter.close();
            System.out.println("File created successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void overwriteBaseCss() throws IOException {
        List <String> cssExternal = getCssExternal();
        String currentPage = driver.getTitle();
        String url = driver.getCurrentUrl();
        String fileName = "src/test/resources/" + currentPage +"/"+ getNamePage(url) + "/"+ currentPage + "_" + getNamePage(url) + "_base.css";
        String filePath = fileName;
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs(); // crea las carpetas necesarias si no existen
        }
        if (file.exists()) {
            file.delete(); // elimina el archivo base si ya existe
        }
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            for (String css : cssExternal) {
                fileWriter.write(css + "\n");
            }
            fileWriter.close();
            System.out.println("Base CSS file overwritten successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error overwriting base file: " + e.getMessage());
            throw new RuntimeException(e);
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

    public List<String> getCssExternal() throws IOException {
        List<String> externalCssUrls = getExternalCssUrls();
        List<String> externalCssContents = new ArrayList<>();
        for (String cssUrl : externalCssUrls) {
            externalCssContents.add(getCssContent(cssUrl));
        }
            return externalCssContents;
    }

    private List<String> getExternalCssUrls() {
        List<WebElement> links = driver.findElements(By.tagName("link"));
        if (links.isEmpty()) {
            throw new IllegalArgumentException("List of links is empty");
        }
        List<String> externalCssUrls = new ArrayList<>();
        for (WebElement link : links) {
            if (Objects.requireNonNull(link.getDomAttribute("rel")).equals("stylesheet")) {
                externalCssUrls.add(link.getDomProperty("href"));
            }
        }
        return externalCssUrls;
    }

    private String getCssContent(String url) throws IOException {
        URL cssUrl = new URL(url);
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(cssUrl.openStream()));
        StringBuilder cssContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            cssContent.append(line).append("\n");
        }
        reader.close();
        return cssContent.toString();
    }

    public List<String> compareCss(List<String> oldCss, List<String> newCss) {
        List<String> discrepancies = new ArrayList<>();
        int minSize = Math.min(oldCss.size(), newCss.size());
        for (int i = 0; i < minSize; i++) {
            String oldCssContent = oldCss.get(i);
            String newCssContent = newCss.get(i);
            if (!StringUtils.equals(oldCssContent, newCssContent)) {
                discrepancies.add("CSS Changes " + (i + 1) + ":");
                discrepancies.add("Old Content: --->" + oldCssContent);
                discrepancies.add("New Content: ---> " + newCssContent);
            }
        }
        for (int i = minSize; i < oldCss.size(); i++) {
            String oldCssContent = oldCss.get(i);
            discrepancies.add("CSS Changes " + (i + 1) + ":");
            discrepancies.add(oldCssContent);
        }
        if (discrepancies.isEmpty()) {
            discrepancies.add("No changes in external CSS.");
    }
        return discrepancies;
}
}