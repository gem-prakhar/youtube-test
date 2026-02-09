Feature: YouTube Search Validation
  As a user
  I want to navigate to YouTube
  So that I can validate the search placeholder text

  Scenario: Validate search placeholder text on YouTube homepage
    Given the user navigates to YouTube homepage
    Then the user should see the text "Try searching to get started"


  Scenario: Next one
    Given the user navigates to YouTube homepage
    When the user waits for 3 seconds
    Then the user should see the text "Try searching to get started"

