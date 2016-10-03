Feature: Gherkin Feature Parser Error
  As a user
  I want to see my issue when Gherkin feature parser unable to parse feature
  So that I can see when my feature ugly and may fix it very soon

  Background:
    Given I have a gherkin lang plugin
    And I have Qulity Profile for plugin
    And Setup up plugin testing environ for bad testdata

  Scenario: Simple Bad Feature
    When I set settings to analyze single file "GherkinParseError.feature"
    Then sensor contains 1 issues
    And I have an issue with rules key "ParserError" on file "GherkinParseError.feature"
