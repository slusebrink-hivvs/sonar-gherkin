Feature: Compute Bussines Analysist Metrics
  As a user
  I want to see the metrics that indroduce requirements management process quality
  So what I impove my project quality and reduce the project tech debt

  Background:
    Given I have a gherkin lang plugin
    And I have Quality Profile for plugin
    And Setup up plugin testing environ

  @AnalystMetrics
  Scenario: Compure personal Gherkin Metrics
    When I set settings to analyze single file "GherkinAnalyticsMetrics.feature"
    Then number of "features_count" in "GherkinAnalyticsMetrics.feature" is 1
    # i think what background is not scenario, but in Gherkin4 backgroud is has Scenario Definition Type
    And number of "scenario_count" in "GherkinAnalyticsMetrics.feature" is 2
    And number of "steps_count" in "GherkinAnalyticsMetrics.feature" is 7

  #TODO - we need an Average Steps in Scenario, Average Scenario in Features
