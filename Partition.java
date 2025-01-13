Feature: Community Role Creation for CSR

  Scenario: Create a Read-Only Community Role for CSR
    Given the LRA is successfully logged into the UMS application
    And the LRA has navigated to the Community Role creation page

    When the LRA selects the option to create a new Community Role
    Then the LRA should have two options to create a read-only role:
      | Option                  | Permissions |
      | Read-only Permission    | Read Retail, Read Commercial      |
      | Role Without Actions    | Write Retail |

    And the LRA completes and saves the role configuration
    Then the system should confirm that the role has been created with the specified permissions or without any permissions


Feature: View a User as CSR with Read-only Permissions

  Scenario: CSR views a user's profile with read-only permissions
    Given the CSR user is successfully logged into the UMS application

    When the CSR navigates to the Administration section
    And the CSR clicks on the 'Search' tab on the manage user page
    Then the UMS application redirects the CSR to the search user page

    When the CSR searches for a user
    And the system displays a list of search results
    And the CSR selects the desired user from the search results
    Then the CSR can view the selected user's profile

    And the CSR can view all user information
    But the action drop-down list is not displayed
    Then no actions can be performed on the user
