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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class GherkinSquidSensorTest {

  private final File baseDir = new File("");
  private SensorContextTester context;
  private GherkinSquidSensor sensor;

  @Before
  public void init() {
    context = SensorContextTester.create(baseDir);
    sensor = new GherkinSquidSensor(context.fileSystem());
  }

  @Test
  public void testDescribe() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    sensor.describe(descriptor);

    assertThat(descriptor.name()).isEqualTo("Gherkin Squid Sensor");
    assertThat(descriptor.languages()).containsOnly("gherkin");
    assertThat(descriptor.type()).isEqualTo(Type.MAIN);
  }

}
