// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.frc6423.lib.driver.CommandRobot;

public class Robot extends CommandRobot {

  public Robot() {}

  @Override
  protected Command getAutonCommand() {
    return Commands.none();
  }

  @Override
  public void close() {
    super.close();
  }
}
