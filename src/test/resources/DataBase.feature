Feature: Random Tests for Database Outputs

  Background:
  Given base connection established

  @db
  Scenario: Verify Random Database Outputs
    Given following query "employee" is executed
    When I retrieve a random record from the database
    Then then verify single data that is matching

  @db
  Scenario: Ensure Random Record Existence
    Given following query "product" is executed
    When I randomly select a record then store it in a map
    Then assert that data provided matches record exists in the database

