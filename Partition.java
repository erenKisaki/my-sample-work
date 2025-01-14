Success Test Cases
CSR Successfully Logs In

Pre-condition: CSR credentials are valid.
Steps:
Enter valid username and password.
Click "Login."
Expected Result: CSR is logged into the UMS application and redirected to the dashboard.
CSR Navigates to Administration Section

Pre-condition: CSR is logged into the UMS application.
Steps:
Click the "Search" tab under Administration.
Expected Result: CSR is redirected to the search user page.
CSR Searches for a Valid User

Pre-condition: The user exists in the system.
Steps:
Enter a valid search query in the search bar.
Click "Search."
Expected Result: Search results display users matching the query.
CSR Selects a User

Pre-condition: Search results are displayed.
Steps:
Click on the desired user's profile link.
Expected Result: The selected user's profile is displayed.
CSR Views User Profile with Read-only Permissions

Pre-condition: CSR has read-only permissions.
Steps:
Navigate through the user's profile sections.
Expected Result: CSR can view all information without any "action" options available.
CSR Cannot Perform Actions

Pre-condition: CSR is assigned a role with no actionable permissions.
Steps:
Attempt to perform an action (e.g., edit, delete).
Expected Result: The action drop-down list is not shown, and no modifications can be made.
Failure Test Cases
CSR Fails to Log In with Invalid Credentials

Pre-condition: CSR credentials are invalid.
Steps:
Enter incorrect username and/or password.
Click "Login."
Expected Result: System displays an error message, and login is denied.
Navigation to Administration Section Fails

Pre-condition: CSR has restricted navigation permissions.
Steps:
Attempt to click the "Search" tab.
Expected Result: CSR receives an error or access denial message.
Search Fails Due to Invalid Query

Pre-condition: Search query is invalid (e.g., special characters or invalid formats).
Steps:
Enter an invalid search query.
Click "Search."
Expected Result: System displays a "No results found" or validation error message.
Search Fails for Non-existent User

Pre-condition: The user does not exist in the system.
Steps:
Enter a valid search query that matches no users.
Click "Search."
Expected Result: System displays a "User not found" message.
CSR Cannot Access User Profile

Pre-condition: CSR lacks permissions to view user profiles.
Steps:
Click on the user link in the search results.
Expected Result: System denies access and displays a permissions error.
Profile Data Fails to Load

Pre-condition: The system encounters a server or database issue.
Steps:
Select a user from the search results.
Expected Result: System displays a "Data could not be loaded" error.
CSR Attempts Unauthorized Actions

Pre-condition: CSR role is read-only.
Steps:
Attempt to perform an action (e.g., edit or delete).
Expected Result: System displays a "Permission Denied" message, and the action is blocked.
Search Page Fails to Load

Pre-condition: CSR is on the administration page.
Steps:
Click the "Search" tab.
Expected Result: System displays a "Page not found" or "Error loading page" message.
