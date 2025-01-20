Scenario: LRA can create sub-country groups with multiple countries
  Given a country group exists with more than one country
  And an LRA is assigned to this country group
  When the LRA logs into the UMS
  And navigates to the "Country Group" tab in the administration section
  And clicks on the "Create sub-country group" option
  And provides valid details to create a sub-country group
  Then the sub-country group is successfully created
  
Scenario: LRA cannot create sub-country groups with only one country
  Given a country group exists with only one country
  And an LRA is assigned to this country group
  When the LRA logs into the UMS
  And navigates to the "Country Group" tab in the administration section
  And clicks on the "Create sub-country group" option
  Then the system disables the option to create a sub-country group
