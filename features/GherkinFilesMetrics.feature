Feature: Gherkin Files Metrics
  As a user
  I want to use sonarqube with Gherkin files support
  So that I manage tech debt on my bussiness analyze process

  Background:
    Given I have a gherkin lang plugin
    And I have Qulity Profile for plugin
    And Setup up plugin testing environ

  Scenario: Compute single file metrics
    When I set settings to analyze single file "GherkinFilesMetrics.feature"
    Then number of "ncloc" in "GherkinFilesMetrics.feature" is 18
    And number of "classes" in "GherkinFilesMetrics.feature" is 1
    And number of "functions" in "GherkinFilesMetrics.feature" is 2
    And number of "statements" in "GherkinFilesMetrics.feature" is 9
    And sensor contains 29 issues
