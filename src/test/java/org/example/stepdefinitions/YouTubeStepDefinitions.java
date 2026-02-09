package org.example.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import org.example.steps.YouTubeSteps;

public class YouTubeStepDefinitions {

    @Steps
    YouTubeSteps youtubeSteps;

    @Given("the user navigates to YouTube homepage")
    public void theUserNavigatesToYouTubeHomepage() {
        youtubeSteps.openYouTubeHomePage();
    }

    @When("the user waits for {int} seconds")
    public void theUserWaitsForSeconds(int seconds) {
        youtubeSteps.waitForGivenSeconds(seconds);
    }

    @Then("the user should see the text {string}")
    public void theUserShouldSeeTheText(String expectedText) {
        youtubeSteps.verifySearchPlaceholderText(expectedText);
    }
}

