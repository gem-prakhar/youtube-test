package org.example.steps;

import net.serenitybdd.core.Serenity;
import org.example.pages.YouTubeHomePage;

public class YouTubeSteps {

    private YouTubeHomePage youtubeHomePage;

    public YouTubeSteps() {
        this.youtubeHomePage = new YouTubeHomePage();
    }

    public void openYouTubeHomePage() {
        System.out.println("RUN TIMESTAMP: " + System.currentTimeMillis());
        youtubeHomePage.openYouTubePage();
        Serenity.recordReportData().withTitle("Navigation").andContents("Navigated to YouTube homepage");
    }

    public void waitForGivenSeconds(int seconds) {
        long startTime = System.currentTimeMillis();
        youtubeHomePage.waitForSeconds(seconds);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Serenity.recordReportData().withTitle("Wait Time").andContents(
            String.format("Waited for %d seconds (actual: %d ms)", seconds, duration));
    }

    public void verifySearchPlaceholderText(String expectedText) {
        boolean isVisible = youtubeHomePage.isSearchPlaceholderTextVisible(expectedText);

        if (isVisible) {
            Serenity.recordReportData().withTitle("Validation").andContents(
                "Successfully found the text: " + expectedText);
        } else {
            String actualText = youtubeHomePage.getSearchPlaceholderText();
            Serenity.recordReportData().withTitle("Validation Failed").andContents(
                "Expected text: " + expectedText + ", Actual text found: " + actualText);
            throw new AssertionError("Search placeholder text '" + expectedText + "' was not found on the page");
        }
    }
}

