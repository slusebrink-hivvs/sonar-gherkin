# SonarQube Gherkin Lang Support

implements continuous code inspect for Gherkin (BDD) Lang

[![GitHub release](https://img.shields.io/github/release/silverbulleters/sonar-gherkin.svg?maxAge=2592000)](https://github.com/silverbulleters/sonar-gherkin/releases)
[![Build Status](http://ci.silverbulleters.org/buildStatus/icon?job=Sonar-Gherkin-Build)](http://ci.silverbulleters.org/job/Sonar-Gherkin-Build/)
[![SonarQube Tech Debt](https://img.shields.io/sonar/https/sonar.silverbulleters.org/sonar-gherkin-plugin/tech_debt.svg?maxAge=2592000)](https://sonar.silverbulleters.org/component_measures/metric/sqale_index/list?id=sonar-gherkin-plugin)
[![SonarQube Coverage](https://img.shields.io/sonar/https/sonar.silverbulleters.org/sonar-gherkin-plugin/coverage.svg?maxAge=2592000)](https://sonar.silverbulleters.org/component_measures/domain/Coverage?id=sonar-gherkin-plugin)


![this is real screenshot](./docs/gherkin-scanner.png)

## Features

* Gherkin 4 Parser - create an issue if feature is not correct writen
* Spell Check English and Russian - create an issue when developer had a mistakes in typo

## Metrics

* compute classes, function and statements as feature, scenario and steps
* compute count of features, scenarios and steps as design domain metrics

## Gherkin Lint

* after cucumber.io released official Gherkin Lint - we implements there result through the event API
  
## Spell Check on i18n

* spellchecker implementation based on https://languagetool.org/

## License and contribute

* GNU

if you want to contribute:

* do not forget write Cucumber-JVM Tests or JUnit Tests 
* use github-flow as a process - fork, commit, pull-request with feature-branching
* fix the sonarqube issues in your pull request
