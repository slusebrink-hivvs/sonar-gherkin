/*
 * Gherkin (Cucumber)
 * Support Gherkin language for SonarQube
 * Copyright (C) 2016 SilverBulleters, LLC
 *
 * mailto:contact AT silverbulleters DOT org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.silverbulleters.sonar.plugins.bdd.steps;

import com.google.common.collect.Lists;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.silverbulleters.sonar.plugins.gherkin.GherkinLangPlugin;
import org.silverbulleters.sonar.plugins.gherkin.GherkinQualityProfile;
import org.silverbulleters.sonar.plugins.gherkin.GherkinRulesDefinition;
import org.silverbulleters.sonar.plugins.gherkin.squid.GherkinSquidSensor;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.internal.apachecommons.codec.Charsets;
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

public class GherkinSteps {

  private GherkinLangPlugin plugin;
  private GherkinQualityProfile profile;
  private SensorContextTester context;
  private File moduleBaseDir;
  private String currentComponentKey;

  private InputFile inputFile(String relativePath) {
    DefaultInputFile inputFile = new DefaultInputFile(this.currentComponentKey, relativePath)
            .setModuleBaseDir(moduleBaseDir.toPath())
            .setLanguage("gherkin")
            .setType(InputFile.Type.MAIN);

    inputFile.initMetadata(new FileMetadata().readMetadata(inputFile.file(), Charsets.UTF_8));
    context.fileSystem().add(inputFile);

    return inputFile;
  }

  private GherkinSquidSensor createSensor() {
    return new GherkinSquidSensor(context.fileSystem());
  }

  @Given("^I have a gherkin lang plugin$")
  public void i_have_a_gherkin_lang_plugin() throws Throwable {
    this.plugin = new GherkinLangPlugin();
  }

  @Given("^I have Qulity Profile for plugin$")
  public void i_have_Qulity_Profile_for_plugin() throws Throwable {
    //throw new NotImplementedException();
    //this.profile = new GherkinQualityProfile();
  }

  @Given("^I have Quality Profile for plugin$")
  public void i_have_Quality_Profile_for_plugin() throws Throwable {
    i_have_Qulity_Profile_for_plugin();
    //TODO this is special method to check spellcheck - remove it wthen we write a special feature with error
  }

  private void setupAbstractSensor() {

    this.context = SensorContextTester.create(this.moduleBaseDir);

    ActiveRulesBuilder _rulesBuilder = new ActiveRulesBuilder();

    NewActiveRule _spellCheck = _rulesBuilder.create(
            RuleKey.of(GherkinRulesDefinition.REPOSITORY_NAME, "SpellCheck")
    );
    _spellCheck.setParam("disabledRules", "WHITESPACE_RULE");

    NewActiveRule _parserErrorCheck = _rulesBuilder.create(
            RuleKey.of(GherkinRulesDefinition.REPOSITORY_NAME, "ParserError")
    );

    ActiveRules activeRules = new DefaultActiveRules(Lists.newArrayList(_parserErrorCheck,_spellCheck));

    this.context.setActiveRules(activeRules);
  }

  @Given("^Setup up plugin testing environ$")
  public void setup_up_plugin_testing_environ() throws Throwable {
    this.moduleBaseDir = new File("features/");
    this.currentComponentKey = "projectKey";

    setupAbstractSensor();
  }

  @Given("^Setup up plugin testing environ for bad testdata$")
  public void setup_up_plugin_testing_environ_for_bad_testdata() throws Throwable {
    this.moduleBaseDir = new File("testdata/bad");
    this.currentComponentKey = "projectKey";

    setupAbstractSensor();
  }

  @Given("^Setup up plugin testing environ for l10n testdata$")
  public void setup_up_plugin_testing_environ_for_l10n_testdata() throws Throwable {
    this.moduleBaseDir = new File("testdata/l10n");
    this.currentComponentKey = "projectKey";

    setupAbstractSensor();
  }

  @When("^I set settings to analyze single file \"([^\"]*)\"$")
  public void i_set_settings_to_analyze(String relativePath) throws Throwable {
    inputFile(relativePath);

    createSensor().execute((SensorContext) this.context);
  }

  @Then("^number of \"([^\"]*)\" in \"([^\"]*)\" is (\\d+)$")
  public void numberOfInIs(String coreMetricName, String fileName, int valueOfCoreMetric) throws Throwable {

    String fileKey = this.currentComponentKey + ":" + fileName;
    assertThat(this.context.measure(fileKey, coreMetricName).value()).isEqualTo(valueOfCoreMetric);
  }

  @Then("^sensor contains (\\d+) issues$")
  public void sensorContainsIssues(int numberOfIssues) throws Throwable {
    Iterator<Issue> issues = this.context.allIssues().iterator();
    assertThat(issues).hasSize(numberOfIssues);
  }

  @Then("^I have an issue with rules key \"([^\"]*)\" on file \"([^\"]*)\"$")
  public void i_have_an_issue_with_rules_key_on_file(String rulesKey, String fileName) throws Throwable {

    Issue findedIssue = null;

    for (Issue _issue: this.context.allIssues()
         ) {

      if (_issue.ruleKey().rule().equals(rulesKey)) {
        InputFile ruleInputFile = ((InputFile)_issue.primaryLocation().inputComponent());
        if (ruleInputFile.language().equals("gherkin")) {
          if (ruleInputFile.relativePath().equals(fileName)) {
            findedIssue = _issue;
            break;
          }
        }
      }
    }

    assertThat(findedIssue).isNotNull();

  }

  @Then("^I have an issue with rules key \"([^\"]*)\" on file \"([^\"]*)\" with message \"([^\"]*)\"$")
  public void i_have_an_issue_with_rules_key_on_file_with_message(String rulesKey, String fileName, String issueMessage) throws Throwable {

    Issue findedIssue = null;
    String findedMessage = null;

    for (Issue _issue: this.context.allIssues()
            ) {

      if (_issue.ruleKey().rule().equals(rulesKey)) {
        InputFile ruleInputFile = ((InputFile)_issue.primaryLocation().inputComponent());
        if (ruleInputFile.language().equals("gherkin")) {
          if (ruleInputFile.relativePath().equals(fileName)) {
            findedIssue = _issue;
            if (_issue.primaryLocation().message().equals(issueMessage)) {
              findedMessage = _issue.primaryLocation().message();
              break;
            }
          }
        }
      }
    }


    assertThat(findedIssue).isNotNull();
    assertThat(findedMessage).isNotNull();
    assertThat(findedMessage).isEqualToIgnoringCase(issueMessage);

  }

}
