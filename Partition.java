
Feature: User Search Functionality UI
  As a user, I want to search for users using various criteria so that I can view their details.
  
  Scenario: User Should have access to Search User Page in UMS Application
    When user login to UMS as CSR and enter the credentails
	Then User successfully able to login
	And User naviagates to the Search User Page
	Then user search page is displayed
	
  Scenario: User don't have access to Search User Page in UMS Application
    When user login to UMS as CSR and enter the credentails
	Then User successfully able to login
	And User tries to navigates to the Search User Page
	Then we got the unauthorized access page

  Scenario: Search by username
    When I enter "testuser" in the "Username" field
    And I click the "Search" button
    Then the user service API call is failed due to Unauthorized access
    Please validare whether the user has access to search service

  Scenario: Search by username
    When I enter "testuser" in the "Username" field
    And I click the "Search" button
    Then the user service API should be called with "username=testuser"
    And the search results should display the relevant user(s)

  Scenario: Search by first name
    When I enter "John" in the "First Name" field
    And I click the "Search" button
    Then the user service API should be called with "firstName=John"
    And the search results should display the relevant user(s)

  Scenario: Search by customer ID
    When I enter "12345" in the "Customer ID" field
    And I click the "Search" button
    Then the user service API should be called with "customerId=12345"
    And the search results should display the relevant user(s)

  Scenario: Search by last name
    When I enter "Doe" in the "Last Name" field
    And I click the "Search" button
    Then the user service API should be called with "lastName=Doe"
    And the search results should display the relevant user(s)

  Scenario: Search by Auth0 ID
    When I enter "auth123" in the "Auth0 ID" field
    And I click the "Search" button
    Then the user service API should be called with "auth0Id=auth123"
    And the search results should display the relevant user(s)

  Scenario: Search by status
    When I select "Active" from the "Status" dropdown
    And I click the "Search" button
    Then the user service API should be called with "status=Active"
    And the search results should display the relevant user(s)

  Scenario: Search by country
    When I select "USA" from the "Country" dropdown
    And I click the "Search" button
    Then the user service API should be called with "country=USA"
    And the search results should display the relevant user(s)

  Scenario: Search by community
    When I select "Community A" from the "Community" dropdown
    And I click the "Search" button
    Then the user service API should be called with "community=Community A"
    And the search results should display the relevant user(s)

  Scenario: Reset search fields
    When I click the "Reset" button
    Then all input fields and dropdowns should be cleared
    And no API call should be made

  Scenario: Search with multiple criteria
    When I enter "testuser" in the "Username" field
    And I select "Active" from the "Status" dropdown
    And I click the "Search" button
    Then the user service API should be called with "username=testuser&status=Active"
    And the search results should display the relevant user(s)
	
  Scenario: Invalid Search Criteria options
    When I enter "testuser" in the "Username" field
    And I select "Active" from the "Status" dropdown
    And I click the "Search" button
    Then the user service API should be called all input data
    And "No results found" is displayed
	
  Scenario: Invalid Search Criteria options
    When I enter "testuser" in the "Username" field
    And I select "Active" from the "Status" dropdown
    And I click the "Search" button
    Then the user service API is failed with Bad Request Response
    Please validate the data to service call input from the UI
	
