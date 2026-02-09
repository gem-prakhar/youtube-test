package org.example.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;

public class YouTubeHomePage extends PageObject {

    @FindBy(xpath = "//input[@id='search']")
    private WebElement searchBox;

    @FindBy(xpath = "//*[contains(text(), 'Try searching to get started')]")
    private WebElement searchPlaceholderText;

    public void openYouTubePage() {
        getDriver().get("https://www.youtube.com/");
        getDriver().manage().window().maximize();
    }

    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Wait interrupted", e);
        }
    }

    public boolean isSearchPlaceholderTextVisible(String expectedText) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

            WebElement element = null;

            try {
                System.out.println("finding element with text: " + expectedText);
                element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//*[contains(text(), 'Try searching to get started')])[2]")));
            } catch (Exception e1) {
                try {
                    element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//ytd-searchbox//div[contains(@class, 'sbsb_a') or contains(@class, 'search')]//descendant::*[contains(text(), '" + expectedText + "')]")));
                } catch (Exception e2) {
                    WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")));
                    String placeholder = searchInput.getAttribute("placeholder");
                    String ariaLabel = searchInput.getAttribute("aria-label");

                    if ((placeholder != null && placeholder.contains(expectedText)) ||
                        (ariaLabel != null && ariaLabel.contains(expectedText))) {
                        return true;
                    }
                }
            }

            return element != null && element.isDisplayed();
        } catch (Exception e) {
            System.out.println("Error finding search placeholder text: " + e.getMessage());
            return false;
        }
    }

    public String getSearchPlaceholderText() {
        try {
            if (searchPlaceholderText.isDisplayed()) {
                return searchPlaceholderText.getText();
            }
        } catch (Exception e) {
            // Try getting placeholder attribute from search box
            try {
                return searchBox.getAttribute("placeholder");
            } catch (Exception ex) {
                System.out.println("Error getting placeholder text: " + ex.getMessage());
            }
        }
        return "";
    }
}

