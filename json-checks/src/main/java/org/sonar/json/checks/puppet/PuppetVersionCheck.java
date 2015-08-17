/*
 * SonarQube JSON Plugin
 * Copyright (C) 2015 David RACODON
 * david.racodon@gmail.com
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.json.checks.puppet;

import com.sonar.sslr.api.AstNode;

import java.util.regex.Pattern;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.json.checks.CheckUtils;
import org.sonar.json.checks.Tags;
import org.sonar.json.parser.JSONGrammar;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "puppet-version",
  name = "\"metadata.json\" version should be a semantic version",
  priority = Priority.MAJOR,
  tags = {Tags.CONVENTION, Tags.PUPPET})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("5min")
public class PuppetVersionCheck extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(JSONGrammar.PAIR);
  }

  @Override
  public void visitNode(AstNode node) {
    if (getContext().getFile().getAbsolutePath().endsWith("metadata.json")
      && "version".equals(CheckUtils.getUnquotedString(node.getFirstChild(JSONGrammar.KEY).getTokenValue()))
      && !Pattern.compile("^\\d+\\.\\d+\\.\\d+$").matcher(CheckUtils.getUnquotedString(node.getFirstChild(JSONGrammar.VALUE).getTokenValue())).matches()) {
      getContext().createLineViolation(this, "Define the version as a semantic version on 3 digits separated by dots: ^\\d+\\.\\d+\\.\\d+$", node.getFirstChild(JSONGrammar.VALUE));
    }
  }

}
