Migration from Hibernate 5 to 6 with JPA Criteria API
===

As usage of the deprecated Hibernate's Criteria API prevents us from upgrading the framework version, one of possible solutions is to make it in 3 steps.
 
###3 steps of migration:

1. If it is possible, to make unit tests for the functionality, which has to be modified (or make sure that we have tests). It's preferable to use TDD when we create tests based on existing interfaces, which will remain. Maybe we will need to refactor existing code to encapsulate particular implementation related to usage of deprecated API.
2. Create alternative implementation using JPA Criteria API. The tests created during step 1 will help us as well as analysis of hibernate logs for the Old and for the New implementation.
3. Move to the latest version of Hibernate using new implementation of Criteria API.

###The example of some simple JPA Criteria API which works fine for both 5 and 6 versions of Hibernate can be found in the `AppTest.class`, represents selection from DB the `Departments` which have `Employees` whose names start with the given string. JPA Criteria API has tools for much more complicated requests. But it is really hard to give any more accurate estimates and proposals on this project without a profound knowledge of the domain area and business processes.
