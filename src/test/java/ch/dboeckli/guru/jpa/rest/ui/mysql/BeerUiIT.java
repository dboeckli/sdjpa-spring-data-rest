package ch.dboeckli.guru.jpa.rest.ui.mysql;

import ch.dboeckli.guru.jpa.rest.domain.BeerStyleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static ch.dboeckli.guru.jpa.rest.ui.BeerWebController.BEER_PAGE;
import static ch.dboeckli.guru.jpa.rest.ui.BeerWebController.LIST_BEERS_PAGE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
class BeerUiIT {

    @LocalServerPort
    private int port;

    private WebDriver webDriver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Run in headless mode
        webDriver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    @Test
    @Order(0)
    void testBeerListPageLoads() {
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();
        assertEquals("Beer List", webDriver.getTitle());
    }

    @Test
    @Order(1)
    void testBeerListContainsItems() {
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        List<WebElement> beerRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#beerTable tbody tr")));

        log.info("### Found {} beer rows", beerRows.size());

        assertFalse(beerRows.isEmpty(), "Beer list should contain items");
        assertEquals(25, beerRows.size());
    }

    @Test
    @Order(3)
    void testSearchByBeerName() {
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        WebElement beerNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerName")));
        beerNameInput.sendKeys("Galaxy Cat");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        searchButton.click();

        waitForPageLoad();

        List<WebElement> beerRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#beerTable tbody tr")));
        assertFalse(beerRows.isEmpty(), "Search results should not be empty");

        for (WebElement row : beerRows) {
            WebElement nameElement = row.findElement(By.cssSelector("td[id^='beerName-']"));
            assertTrue(nameElement.getText().toLowerCase().contains("galaxy cat"), "All results should contain 'Galaxy Cat' in the name");
        }
    }

    @Test
    @Order(4)
    void testSearchByBeerStyle() {
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        WebElement beerStyleSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerStyle")));
        beerStyleSelect.sendKeys(BeerStyleEnum.PALE_ALE.name());

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        searchButton.click();

        waitForPageLoad();

        List<WebElement> beerRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#beerTable tbody tr")));
        assertFalse(beerRows.isEmpty(), "Search results should not be empty");

        for (WebElement row : beerRows) {
            WebElement styleElement = row.findElement(By.cssSelector("td[id^='beerStyle-']"));
            assertEquals(BeerStyleEnum.PALE_ALE.name(), styleElement.getText(), "All results should have PALE_ALE style");
        }
    }

    @Test
    @Order(5)
    void testSearchByBeerNameAndStyle() {
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        WebElement beerNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerName")));
        beerNameInput.sendKeys("Galaxy Cat");

        WebElement beerStyleSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerStyle")));
        beerStyleSelect.sendKeys(BeerStyleEnum.PALE_ALE.name());

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        searchButton.click();

        waitForPageLoad();

        List<WebElement> beerRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#beerTable tbody tr")));
        assertFalse(beerRows.isEmpty(), "Search results should not be empty");

        for (WebElement row : beerRows) {
            WebElement nameElement = row.findElement(By.cssSelector("td[id^='beerName-']"));
            WebElement styleElement = row.findElement(By.cssSelector("td[id^='beerStyle-']"));
            assertTrue(nameElement.getText().toLowerCase().contains("galaxy cat"), "All results should contain 'Galaxy Cat' in the name");
            assertEquals(BeerStyleEnum.PALE_ALE.name(), styleElement.getText(), "All results should have PALE_ALE style");
        }
    }

    @Test
    @Order(6)
    void testSearchByUpc() {
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        // Find and fill the UPC input
        WebElement upcInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("upc")));
        upcInput.clear();
        upcInput.sendKeys("0631234200036");

        // Find and click the "Search by UPC" button
        WebElement searchByUpcButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Search by UPC')]")));
        searchByUpcButton.click();

        waitForPageLoad();

        // Check if we're on the Beer Details page
        wait.until(ExpectedConditions.titleIs("Beer Details"));

        // Check the beer details
        WebElement pageTitle = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pageTitle")));
        assertEquals("Beer Details", pageTitle.getText(), "The page title should be 'Beer Details'");

        WebElement beerNameElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerName")));
        assertEquals("Mango Bobs", beerNameElement.getText(), "The beer name should be 'Mango Bobs'");

        WebElement beerStyleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerStyle")));
        assertEquals("ALE", beerStyleElement.getText(), "The beer style should be 'ALE'");

        WebElement upcElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerUpc")));
        assertEquals("0631234200036", upcElement.getText(), "The UPC should match the search input");

        // Check other details
        WebElement idElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerId")));
        assertFalse(idElement.getText().isEmpty(), "The ID should not be empty");

        WebElement priceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerPrice")));
        assertFalse(priceElement.getText().isEmpty(), "The Price should not be empty");

        WebElement quantityElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("beerQuantity")));
        assertFalse(quantityElement.getText().isEmpty(), "The Quantity On Hand should not be empty");

        // Check if "Edit Beer" and "Delete Beer" buttons are present
        assertTrue(webDriver.findElement(By.id("editBeerButton")).isDisplayed(), "Edit Beer button should be visible");
        assertTrue(webDriver.findElement(By.id("deleteBeerButton")).isDisplayed(), "Delete Beer button should be visible");

        // Check if "Back to Beer List" link is present
        assertTrue(webDriver.findElement(By.id("backToListButton")).isDisplayed(), "Back to Beer List button should be visible");
    }

    @Test
    @Order(97)
    void testEditBeer() {
        // Navigate to the beer list page
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        // Find and click the edit button for the first beer
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[id^='editBeer-']")
        ));
        // Extract the beer ID from the edit button's ID
        String editButtonId = editButton.getAttribute("id");
        String beerId = StringUtils.substringAfter(editButtonId, "editBeer-");

        Actions actions = new Actions(webDriver);
        actions.moveToElement(editButton).click().perform();

        // Wait for the edit page to load
        wait.until(ExpectedConditions.urlContains(BEER_PAGE + "/edit/"));

        // Find the beer name input field and update it
        WebElement beerNameInput = webDriver.findElement(By.id("beerName"));
        String newBeerName = "Updated Beer Name";
        beerNameInput.clear();
        beerNameInput.sendKeys(newBeerName);

        // Submit the form
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        try {
            submitButton.click();
        } catch (Exception e) {
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            executor.executeScript("arguments[0].click();", submitButton);
        }

        // Wait for the beer list page to reload
        wait.until(ExpectedConditions.urlToBe("http://localhost:" + port + LIST_BEERS_PAGE));

        List<WebElement> beerRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#beerTable tbody tr")));
        log.info("Logging all beer names and IDs:");
        HashMap<String, String> beerMap = new HashMap<>();
        for (WebElement row : beerRows) {
            WebElement nameElement = row.findElement(By.cssSelector("td[id^='beerName-']"));
            String id = nameElement.getAttribute("id").replace("beerName-", "");
            String name = nameElement.getText();
            beerMap.put(id, name);
            log.info("#### Beer ID: {}, Name: {}", id, name);
        }
        log.info("### Checking if the beer name has been updated: {}", beerId);
        assertEquals(newBeerName, beerMap.get(beerId), "Beer name should be updated in the beer list page");
    }

    @Test
    @Order(98)
    void testCreateNewBeer() {
        // Navigate to the beer list page
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        // Get the initial total number of beers
        WebElement totalItemsElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("totalItems")));
        int initialTotalItems = Integer.parseInt(totalItemsElement.getText());
        log.info("Initial total items: {}", initialTotalItems);

        // Click on the "Create New Beer" button
        WebElement newBeerButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("createNewBeer")));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(newBeerButton).click().perform();

        // Wait for the new beer form to load
        wait.until(ExpectedConditions.urlContains(BEER_PAGE + "/new"));

        // Fill in the form
        WebElement beerNameInput = webDriver.findElement(By.id("beerName"));
        String newBeerName = "Test New Beer " + System.currentTimeMillis();
        beerNameInput.sendKeys(newBeerName);
        assertEquals(newBeerName, beerNameInput.getAttribute("value"), "Beer name should be set correctly");

        WebElement beerStyleInput = webDriver.findElement(By.id("beerStyle"));
        beerStyleInput.sendKeys("IPA");

        WebElement upcInput = webDriver.findElement(By.id("upc"));
        upcInput.sendKeys("123456789012");

        WebElement priceInput = webDriver.findElement(By.id("price"));
        priceInput.sendKeys("10.99");

        WebElement quantityOnHandInput = webDriver.findElement(By.id("quantityOnHand"));
        quantityOnHandInput.sendKeys("100");

        // Validieren Sie die Formulardaten direkt vor dem Absenden
        String formData = (String) ((JavascriptExecutor) webDriver).executeScript(
            "return Array.from(document.querySelector('form').elements).map(e => e.name + '=' + e.value).join('&');"
        );
        log.info("Form data before submission: {}", formData);
        // Submit the form
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        try {
            submitButton.click();
        } catch (Exception e) {
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            executor.executeScript("arguments[0].click();", submitButton);
        }

        // Wait for the beer list page to reload
        wait.until(ExpectedConditions.urlToBe("http://localhost:" + port + LIST_BEERS_PAGE));

        // Check that the total number of beers has increased by 1
        totalItemsElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("totalItems")));
        int finalTotalItems = Integer.parseInt(totalItemsElement.getText());
        log.info("Final total items: {}", finalTotalItems);

        assertEquals(initialTotalItems + 1, finalTotalItems, "Total number of beers should increase by 1 after creation");

        // Navigate to the next page
        WebElement nextPageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class, 'page-link') and text()='Next']")));
        nextPageButton.click();
        waitForPageLoad();

        // Verify that the new beer is in the list
        List<WebElement> beerNames = webDriver.findElements(By.cssSelector("td[id^='beerName-']"));
        log.info("Beer names on the current page:");
        for (int i = 0; i < beerNames.size(); i++) {
            WebElement beerNameElement = beerNames.get(i);
            String beerName = beerNameElement.getText();
            String beerId = beerNameElement.getAttribute("id").replace("beerName-", "");
            log.info("{}. Beer ID: {}, Name: {}", i + 1, beerId, beerName);
        }
        boolean newBeerExists = beerNames.stream()
            .anyMatch(element -> element.getText().equals(newBeerName));
        assertTrue(newBeerExists, "Newly created beer should be present in the list. newBeerName: " + newBeerName);
    }

    @Test
    @Order(99)
    void testDeleteBeer() {
        // Navigate to the beer list page
        webDriver.get("http://localhost:" + port + LIST_BEERS_PAGE);
        waitForPageLoad();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        // Get the initial total number of beers
        WebElement totalItemsElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("totalItems")));
        int initialTotalItems = Integer.parseInt(totalItemsElement.getText());
        log.info("Initial total items: {}", initialTotalItems);

        // Find the delete button for the first beer
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[id^='deleteBeer-']")
        ));
        String deleteButtonId = deleteButton.getAttribute("id");
        String beerId = StringUtils.substringAfter(deleteButtonId, "deleteBeer-");

        // Log the beer being deleted
        WebElement beerNameElement = webDriver.findElement(By.id("beerName-" + beerId));
        String beerName = beerNameElement.getText();
        log.info("Deleting beer: ID = {}, Name = {}", beerId, beerName);

        // Click the delete button
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", deleteButton);

        // Handle the confirmation dialog
        wait.until(ExpectedConditions.alertIsPresent());
        webDriver.switchTo().alert().accept();

        // Wait for the page to reload
        waitForPageLoad();

        // Check that the total number of beers has decreased by 1
        totalItemsElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("totalItems")));
        int finalTotalItems = Integer.parseInt(totalItemsElement.getText());
        log.info("Final total items: {}", finalTotalItems);

        assertEquals(initialTotalItems - 1, finalTotalItems, "Total number of beers should decrease by 1 after deletion");

        // Verify that the deleted beer is no longer in the list
        List<WebElement> remainingBeerNames = webDriver.findElements(By.cssSelector("td[id^='beerName-']"));
        boolean beerStillExists = remainingBeerNames.stream()
            .anyMatch(element -> element.getText().equals(beerName));
        assertFalse(beerStillExists, "Deleted beer should not be present in the list");
    }

    private void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        wait.until((ExpectedCondition<Boolean>) wd ->
            Objects.equals(((JavascriptExecutor) wd).executeScript("return document.readyState"), "complete"));
    }

}