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
package org.silverbulleters.sonar.plugins.gherkin.measures;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.List;
import static java.util.Arrays.asList;

public class AnalystMetrics implements Metrics {

  public static final Metric<Integer> FEATURES_COUNT =
          new Metric.Builder("features_count", "Gherkin Feature Count", Metric.ValueType.INT)
                  .setDescription("Number of features files with user story")
                  .setDirection(Metric.DIRECTION_BETTER)
                  .setQualitative(false)
                  .setDomain(CoreMetrics.DOMAIN_DESIGN)
                  .create();

  public static final Metric<Integer> SCENARIO_COUNT =
          new Metric.Builder("scenario_count", "Scenario Count", Metric.ValueType.INT)
                  .setDescription("Number of scenario in all feature files")
                  .setDirection(Metric.DIRECTION_BETTER)
                  .setQualitative(false)
                  .setDomain(CoreMetrics.DOMAIN_DESIGN)
                  .create();

  public static final Metric<Integer> STEPS_COUNT =
          new Metric.Builder("steps_count", "Gherkin Step Count", Metric.ValueType.INT)
                  .setDescription("Number of implements in all scenario")
                  .setDirection(Metric.DIRECTION_BETTER)
                  .setQualitative(false)
                  .setDomain(CoreMetrics.DOMAIN_DESIGN)
                  .create();

  @Override
  public List<Metric> getMetrics() {
    return asList(FEATURES_COUNT, SCENARIO_COUNT, STEPS_COUNT);
  }
}
