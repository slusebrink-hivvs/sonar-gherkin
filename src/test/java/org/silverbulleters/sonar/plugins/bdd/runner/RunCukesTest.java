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
package org.silverbulleters.sonar.plugins.bdd.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        format = {"pretty", "html:target/cucumber/html", "json:target/cucumber/result.json"},
        //support standart dir in core path
        features = "features/",
        glue = "org.silverbulleters.sonar.plugins.bdd.steps"

)
public class RunCukesTest {
}