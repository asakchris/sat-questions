package org.example;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

enum Assessment {
  SAT("SAT"),
  PSAT_8_9("PSAT 8/9"),
  PSAT_10_NMSQT("PSAT/NMSQT & PSAT 10");
  private final String value;

  Assessment(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}

enum Test implements TestType {
  MATH("Math"),
  READING_WRITING("Reading and Writing");

  private final String value;

  Test(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getTestType() {
    return value;
  }
}

enum DomainScores implements TestType {
  INFORMATION_AND_IDEAS("checkbox-information and ideas", Test.READING_WRITING),
  CRAFT_AND_STRUCTURE("checkbox-craft and structure", Test.READING_WRITING),
  EXPRESSION_OF_IDEAS("checkbox-expression of ideas", Test.READING_WRITING),
  STANDARD_ENGLISH_CONVENTIONS("checkbox-standard english conventions", Test.READING_WRITING),
  ALGEBRA("checkbox-algebra", Test.MATH),
  ADVANCED_MATH("checkbox-advanced math", Test.MATH),
  PROBLEM_SOLVING_AND_DATA_ANALYSIS("checkbox-problem-solving and data analysis", Test.MATH),
  GEOMETRY_AND_TRIGONOMETRY("checkbox-geometry and trigonometry", Test.MATH);

  private final String value;
  private final Test test;

  DomainScores(String value, Test test) {
    this.value = value;
    this.test = test;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getTestType() {
    return test.getValue();
  }
}

enum ExportType {
  NO_CORRECT_ANSWERS("no-explanations"),
  CORRECT_ANSWERS("with-explanations"),
  WITHOUT_ANSWERS("no-headers");

  private final String value;

  ExportType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}

interface TestType {
  String getTestType();
}

public class Main {

  public static void main(String[] args) {
    Assessment assessment = Assessment.valueOf(System.getProperty("assessment"));
    System.out.println(assessment);
    DomainScores domainScores = DomainScores.valueOf(System.getProperty("domain_scores"));
    System.out.println(domainScores);
    ExportType exportType = ExportType.valueOf(System.getProperty("export_type"));
    System.out.println(exportType);

    // Create the driver
    ChromeOptions options = new ChromeOptions();
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("download.default_directory", "C:\\temp\\sat");
    prefs.put("download.prompt_for_download", false);
    options.setExperimentalOption("prefs", prefs);
    WebDriver driver = new ChromeDriver(options);

    // Open the page
    driver.get("https://satsuitequestionbank.collegeboard.org/digital/search");
    // Wait for the page to load, timeout after 3 seconds
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

//    new WebDriverWait(driver, Duration.ofSeconds(3))
//        .until(ExpectedConditions.presenceOfElementLocated(By.id("selectAssessmentType")));
    System.out.println(driver.getTitle());

    // Select assessment type as SAT Math
    new Select(driver.findElement(By.id("selectAssessmentType")))
        .selectByVisibleText(assessment.getValue());
    // Select test type as Math
    new Select(driver.findElement(By.id("selectTestType")))
        .selectByVisibleText(domainScores.getTestType());
    // Select domain scores as Algebra
    driver.findElement(By.id(domainScores.getValue())).click();
    // Click on the search button
    driver.findElement(By.xpath("//button[text()='Search']")).click();

    // Select all questions
    driver
        .findElements(By.xpath("//input[@type='checkbox'][starts-with(@id, 'questionCheckbox-')]"))
        .forEach(WebElement::click);
    // Click on the export button
    driver.findElement(By.xpath("//button[text()='Export']")).click();
    //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    new WebDriverWait(driver, Duration.ofMinutes(5))
        .until(ExpectedConditions.presenceOfElementLocated(By.id(exportType.getValue())));
    driver.findElement(By.id(exportType.getValue())).click();
    driver.findElement(By.xpath("//button[text()='Export PDF']")).click();
    // Close the driver
    // driver.quit();
  }
}
