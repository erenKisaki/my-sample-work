Given the LRA is logged into the UMS
When the LRA creates a community role with the "activate user" action
Then the role should be saved successfully
And the role should include the "activate user" action


Given the LRA is logged into the UMS
And a community role with the "activate user" action exists
When the LRA assigns the role to a CSR user
Then the CSR should be successfully assigned the role


Given the CSR is assigned a role with the "activate user" action
When the CSR logs into the UMS
And the CSR searches for a customer
Then the CSR should see the "activate user" action in the dropdown menu
And the CSR should not see actions outside their assigned role

Given the CSR is assigned a role with the "activate user" action
When the CSR attempts to perform an action outside their assigned role
Then the action should not be executed
And the CSR should see other actions disabled

Given the CSR is assigned a role with the "activate user" action
And the LRA updates the role to include the "view user" action
When the CSR logs into the UMS
And the CSR searches for a customer
Then the CSR should see both "activate user" and "view user" actions in the dropdown menu

Given the CSR is assigned a role with the "activate user" action
And the CSR can visually see other actions in the dropdown
When the CSR attempts to perform an unauthorized action
Then the system should not execute the action

Given multiple CSR users are assigned roles
When they log in and perform their respective actions concurrently
Then the system should respond without performance degradation
And each CSR should only see the actions assigned to their role

Given the LRA is logged into the UMS  
And a role with the "activate user" action exists  
When the LRA updates the role to include the "view user" action  
Then the role should be updated successfully  
And the changes should reflect immediately for the CSR users assigned to this role  

Given the CSR is assigned a role with the "activate user" action  
And the LRA modifies the role to include the "view user" action  
When the CSR logs back into the UMS  
And the CSR searches for a customer  
Then the CSR should see both "activate user" and "view user" actions in the dropdown menu  
