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
package org.silverbulleters.sonar.plugins.gherkin.squid;

import com.google.common.collect.Lists;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.ParserException;
import gherkin.ast.Feature;
import gherkin.ast.GherkinDocument;
import gherkin.ast.Node;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.Step;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;
import org.silverbulleters.sonar.plugins.gherkin.GherkinLanguage;
import org.silverbulleters.sonar.plugins.gherkin.GherkinRulesDefinition;
import org.silverbulleters.sonar.plugins.gherkin.measures.AnalystMetrics;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.squidbridge.ProgressReport;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class GherkinSquidSensor implements Sensor {

  private static final Logger LOG = Loggers.get(GherkinSquidSensor.class);

  private final FilePredicate mainFilePredicate;

  public GherkinSquidSensor(FileSystem fileSystem){
    this.mainFilePredicate = fileSystem.predicates().and(
            fileSystem.predicates().hasType(InputFile.Type.MAIN),
            fileSystem.predicates().hasLanguage(GherkinLanguage.KEY));
  }

  static String readFile(String path, Charset encoding)
          throws IOException
  {
    byte[] encoded = java.nio.file.Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    sensorDescriptor
            .onlyOnLanguage(GherkinLanguage.KEY)
            .name("Gherkin Squid Sensor")
            .onlyOnFileType(InputFile.Type.MAIN);
  }

  @Override
  public void execute(SensorContext sensorContext) {
    LOG.debug(sensorContext.getSonarQubeVersion() + " started ");

    ProgressReport report = new ProgressReport("Progress report about of Gherkin analyzer", TimeUnit.SECONDS.toMillis(10));

    report.start(Lists.newArrayList(sensorContext.fileSystem().files(mainFilePredicate)));

    analyzeFiles(sensorContext, report);

  }

  @com.google.common.annotations.VisibleForTesting
  protected void analyzeFiles(SensorContext sensorContext, ProgressReport progressReport) {
    boolean success = false;
    try {

      for (InputFile inputFile : sensorContext.fileSystem().inputFiles(mainFilePredicate)){
        analyse(sensorContext, inputFile);
      }

      progressReport.nextFile();
      success = true;

    } catch (CancellationException e) {
      // do not propagate the exception
      LOG.debug(e.toString());
    } finally {
      stopProgressReport(progressReport, success);
    }
  }

  private void analyse(SensorContext sensorContext, InputFile inputFile) {

    Parser<GherkinDocument> parser = new Parser<> (new AstBuilder());

    try {
      int gNLOC = java.nio.file.Files.readAllLines(inputFile.path(), Charset.defaultCharset()).size();

      sensorContext.<Integer>newMeasure()
              .forMetric(CoreMetrics.NCLOC)
              .on(inputFile)
              .withValue(gNLOC+1)
              .save();

      String content = readFile(inputFile.path().toString(), Charset.defaultCharset());

      GherkinDocument doc = parser.parse(content);

      Feature feature = doc.getFeature();

      sensorContext.<Integer>newMeasure()
              .forMetric(CoreMetrics.CLASSES)
              .on(inputFile)
              .withValue(1)
              .save();

      sensorContext.<Integer>newMeasure()
              .forMetric(AnalystMetrics.FEATURES_COUNT)
              .on(inputFile)
              .withValue(1)
              .save();

      List<ScenarioDefinition> scenarioDefinitions = feature.getChildren();
      sensorContext.<Integer>newMeasure()
              .forMetric(CoreMetrics.FUNCTIONS)
              .on(inputFile)
              .withValue(scenarioDefinitions.size())
              .save();

      sensorContext.<Integer>newMeasure()
              .forMetric(AnalystMetrics.SCENARIO_COUNT)
              .on(inputFile)
              .withValue(scenarioDefinitions.size())
              .save();

      int gSteps = 0;
      for (ScenarioDefinition scenario: scenarioDefinitions ) {
        gSteps = gSteps + scenario.getSteps().size();
      }

      sensorContext.<Integer>newMeasure()
              .forMetric(CoreMetrics.STATEMENTS)
              .on(inputFile)
              .withValue(gSteps)
              .save();

      sensorContext.<Integer>newMeasure()
              .forMetric(AnalystMetrics.STEPS_COUNT)
              .on(inputFile)
              .withValue(gSteps)
              .save();

      createSpellCheckIssues(sensorContext, inputFile, feature);

    } catch (IOException e) {
      LOG.debug(e.toString());
    } catch (ParserException pe){
      LOG.debug(pe.toString());
      createParserErrorIssue(sensorContext, inputFile, pe);
    }

  }

  private void createSpellCheckIssues(SensorContext sensorContext, InputFile inputFile, Feature feature) throws IOException {

    // TODO: Don't hardcode the ruleKey
    RuleKey ruleKey = RuleKey.of(GherkinRulesDefinition.REPOSITORY_NAME, "SpellCheck");

    ActiveRule activeRule = sensorContext.activeRules().find(ruleKey);
    if (activeRule == null) {
      LOG.debug("SpellCheck rule is deactivated. RuleKey: " + ruleKey);
      return;
    }

    List<ScenarioDefinition> scenarioDefinitions = feature.getChildren();

    String featureLanguage = feature.getLanguage();
    if ("en".equals(featureLanguage)) {
      featureLanguage = "en-US";
    }

    Language language = Languages.getLanguageForShortName(featureLanguage);
    JLanguageTool langTool = new JLanguageTool(language);


    String disabledRules = activeRule.param("disabledRules");
    String[] disabledRulesArray = new String[0];

    if (disabledRules != null) {
      disabledRulesArray = disabledRules.split(",");
    }

    for (String disabledRuleKey : disabledRulesArray) {
      langTool.disableRule(disabledRuleKey);
    }

    checkNodeSpelling(sensorContext, inputFile, langTool, feature, feature.getName());
    checkNodeSpelling(sensorContext, inputFile, langTool, feature, feature.getDescription());

    for (ScenarioDefinition scenario: scenarioDefinitions ) {
      checkNodeSpelling(sensorContext, inputFile, langTool, scenario, scenario.getName());

      List<Step> steps = scenario.getSteps();
      for (Step step : steps) {
        checkNodeSpelling(sensorContext, inputFile, langTool, step, step.getText());
      }
    }

  }

  private void checkNodeSpelling(SensorContext sensorContext,
                                 InputFile inputFile,
                                 JLanguageTool langTool,
                                 Node node,
                                 String nodeText) throws IOException {
    List<RuleMatch> matches = langTool.check(nodeText);

    for (RuleMatch match : matches) {
      createSpellCheckIssueObject(sensorContext, inputFile, node, match);
    }
  }

  private void createParserErrorIssue(SensorContext sensorContext, InputFile inputFile, ParserException pe) {
    RuleKey ruleKey = RuleKey.of(GherkinRulesDefinition.REPOSITORY_NAME, "ParserError");

    ActiveRule activeRule = sensorContext.activeRules().find(ruleKey);
    if (activeRule == null) {
      LOG.debug("Parser Error rule is deactivated. RuleKey: " + ruleKey);
      return;
    }

    TextRange textRange = inputFile.newRange(1,1,1,1);
    //TODO Parser error message contains an address - add it


    NewIssue newIssue = sensorContext.newIssue();

    NewIssueLocation primaryLocation = newIssue.newLocation()
            .message(pe.getMessage())
            .on(inputFile)
            .at(textRange);

    newIssue.forRule(ruleKey).at(primaryLocation);
    newIssue.save();


  }

  private void createSpellCheckIssueObject(SensorContext sensorContext,
                                           InputFile inputFile,
                                           Node node,
                                           RuleMatch match) {

    // TODO: Don't hardcode the ruleKey
    RuleKey ruleKey = RuleKey.of(GherkinRulesDefinition.REPOSITORY_NAME, "SpellCheck");

    try {

      TextRange textRange = inputFile.newRange(
              node.getLocation().getLine(),
              match.getColumn(),
              node.getLocation().getLine(),
              match.getEndColumn()
      );

      NewIssue newIssue = sensorContext.newIssue();

      String suggestedReplace = "";

      if (!match.getSuggestedReplacements().isEmpty()) {
        suggestedReplace = " - replace it with:";
        for (String suggest: match.getSuggestedReplacements()
                ) {
          suggestedReplace = suggestedReplace + " " + suggest + ";";
        }
      }

      NewIssueLocation primaryLocation = newIssue.newLocation()
              .message(match.getMessage() + suggestedReplace)
              .on(inputFile)
              .at(textRange);

      newIssue.forRule(ruleKey).at(primaryLocation);
      newIssue.save();

    } catch (IllegalArgumentException iarg) {
      LOG.error("SpellCheck rule is an error when create issue: \n" +
              "file is: " + inputFile.relativePath() + "\n" +
              "match is:" + match.getMessage() + "\n" +
              "line is:" + node.getLocation().getLine() + "\n" +
              "offsets is: start - " + match.getColumn() + " to " + match.getEndColumn(),
              iarg);
    }

  }

  private static void stopProgressReport(ProgressReport progressReport, boolean success) {
    if (success) {
      progressReport.stop();
    } else {
      progressReport.cancel();
    }
  }
}


