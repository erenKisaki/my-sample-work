Feature: User Service API
  As a developer, I want to ensure the User Service API behaves as expected for various requests.
  
  Scenario: Access Token validated success
    Given the User Service API is running
    When I send a GET request
    Then the response status should be 403
    And Error Saying Pass the Valid token
	
  Scenario: Invalid Data Passed for Service
    Given the User Service API is running
    When I send a GET request
    Then the response status should be 400
    And Error Saying Invalid input data for the fields 
	
	Scenario: Internal Server Error for service
    Given the User Service API is running
    When I send a GET request
    Then the response status was be 500
    And Error saying exception occured while retrieving the data  

  Scenario: Fetch users by username
    Given the User Service API is running
    When I send a GET request to "/users?username=testuser"
    Then the response status should be 200
    And the response should contain user(s) with "username=testuser"

  Scenario: Fetch users by first name
    Given the User Service API is running
    When I send a GET request to "/users?firstName=John"
    Then the response status should be 200
    And the response should contain user(s) with "firstName=John"

  Scenario: Fetch users by customer ID
    Given the User Service API is running
    When I send a GET request to "/users?customerId=12345"
    Then the response status should be 200
    And the response should contain user(s) with "customerId=12345"

  Scenario: Fetch users by last name
    Given the User Service API is running
    When I send a GET request to "/users?lastName=Doe"
    Then the response status should be 200
    And the response should contain user(s) with "lastName=Doe"

  Scenario: Fetch users by Auth0 ID
    Given the User Service API is running
    When I send a GET request to "/users?auth0Id=auth123"
    Then the response status should be 200
    And the response should contain user(s) with "auth0Id=auth123"

  Scenario: Fetch users by status
    Given the User Service API is running
    When I send a GET request to "/users?status=Active"
    Then the response status should be 200
    And the response should contain user(s) with "status=Active"

  Scenario: Fetch users by country
    Given the User Service API is running
    When I send a GET request to "/users?country=USA"
    Then the response status should be 200
    And the response should contain user(s) with "country=USA"

  Scenario: Fetch users by community
    Given the User Service API is running
    When I send a GET request to "/users?community=Community A"
    Then the response status should be 200
    And the response should contain user(s) with "community=Community A"

  Scenario: Fetch users with multiple criteria
    Given the User Service API is running
    When I send a GET request to "/users?username=testuser&status=Active"
    Then the response status should be 200
    And the response should contain user(s) matching all criteria

  Scenario: Handle invalid input
    Given the User Service API is running
    When I send a GET request to "/users?status=InvalidStatus"
    Then the response status should be 400
    And the response should contain an appropriate error message

  Scenario: Handle no results
    Given the User Service API is running
    When I send a GET request to "/users?username=nonexistentuser"
    Then the response status should be 200
    And the response should contain an empty result set

  Scenario: Handle server errors
    Given the User Service API is running
    When the database is unavailable
    And I send a GET request to "/users"
    Then the response status should be 500
    And the response should contain an appropriate error message
