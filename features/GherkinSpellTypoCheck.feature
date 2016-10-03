Feature: Gherkin Feature Spell Checking
  As a user
  I want to see my issue when i write not correct word in feature
  So that My users will see good documentation without typos

  Background:
    Given I have a gherkin lang plugin
    And I have Qulity Profile for plugin
    And Setup up plugin testing environ for l10n testdata


  @English
  Scenario: Simple English Spell Check
    When I set settings to analyze single file "GherkinSpellCheckEN.feature"
    Then sensor contains 4 issues
    And I have an issue with rules key "SpellCheck" on file "GherkinSpellCheckEN.feature"

  @Russian
  Scenario: Simple Russian Spell Check
    When I set settings to analyze single file "GherkinSpellCheckRU.feature"
    Then sensor contains 2 issues
    And I have an issue with rules key "SpellCheck" on file "GherkinSpellCheckRU.feature"

  @English @Russian
  Scenario Outline: Find Concrete Message With Spell Check
    When I set settings to analyze single file <FileForSpelling>
    Then I have an issue with rules key "SpellCheck" on file <FileForSpelling> with message <RuleMessage>
    Examples:
    | FileForSpelling | RuleMessage      |
    | "GherkinSpellCheckEN.feature" | "Use <suggestion>an</suggestion> instead of 'a' if the following word starts with a vowel sound, e.g. 'an article', 'an hour' - replace it with: an;" |
    | "GherkinSpellCheckEN.feature" | "Possible spelling mistake found - replace it with: user; users; us era; user a;"                                                                     |
    | "GherkinSpellCheckEN.feature" | "Possible spelling mistake found - replace it with: issue; issued; issues; issuer; issuers;"                                                          |
    | "GherkinSpellCheckEN.feature" | "Possible spelling mistake found - replace it with: that; what; w that; wt hat;"                                                                      |
    | "GherkinSpellCheckRU.feature" | "Найдена орфографическая ошибка - replace it with: пользователю; пользователе; пользователи; пользователя; пользователь;"             |
    | "GherkinSpellCheckRU.feature" | "Найдена орфографическая ошибка - replace it with: замётан; замешан; замечаю; замечай; замечал; замечая; замечав; замечен; заме чан;" |
