// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.driver;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.frc6423.lib.hardware.ServoIO;
import org.frc6423.lib.hardware.ServoIO.Setpoint;
import org.frc6423.lib.util.DelayedLatch;

@Logged
public class PositionSubsystem extends MotorSubsystem {
  protected final boolean isTaredSubsystem;
  private final TaringConfig taringConfig;
  private boolean isTared = false;
  private boolean isTaring = false;
  private DelayedLatch tareDelay;
  protected final double epsilon;

  public static class TaringConfig {
    public double taredPosition;
    public double taringVoltage;
    public double taringTimeoutSeconds;
    public double taredVelocity;
  }

  public PositionSubsystem(
      ServoIO hardware, boolean isTaredSubsystem, TaringConfig taringConfig, double epsilon) {
    super(hardware);
    this.isTaredSubsystem = isTaredSubsystem;
    this.taringConfig = taringConfig;
    this.epsilon = epsilon;
  }

  @Override
  public void periodic() {
    super.periodic();
    if (isTaredSubsystem) {
      if (!isTared) {
        isTaring = true;
        hardware.enableSoftLimits(false);
        tareDelay = new DelayedLatch(Timer.getFPGATimestamp(), taringConfig.taringTimeoutSeconds);
      }
      if (isTaring) {
        applySetpoint(Setpoint.withVoltage(taringConfig.taringVoltage));
        if (tareDelay.update(
                Timer.getFPGATimestamp(), Math.abs(getVelocity()) < taringConfig.taredVelocity)
            && DriverStation.isEnabled()) {
          setPosition(taringConfig.taredPosition);
          applySetpoint(getCurrentSetpoint());
          hardware.enableSoftLimits(false);
          isTared = true;
        }
      }
    }
  }

  public boolean nearPositionSetpoint() {
    return getCurrentSetpoint().isPositionSetpoint() && nearPosition(getSetpointValue());
  }

  public boolean nearPosition(double position) {
    return MathUtil.isNear(position, getPosition(), epsilon);
  }

  public double getSetpointValue() {
    return getCurrentSetpoint().getSetpointValue();
  }

  @Override
  protected void applySetpoint(Setpoint setpoint) {
    super.applySetpoint(setpoint);
  }

  public Command runSetpointUntilNearPositionCmd(Setpoint setpoint) {
    return waitUntilNearPositionCmd(setpoint.getSetpointValue())
        .withDeadline(runSetpointCmd(() -> setpoint))
        .withName(
            getName()
                + " RUN UNITL "
                + getSetpointValue()
                + " "
                + getCurrentSetpoint().getSetpointType());
  }

  public Command waitUntilNearPositionCmd(double position) {
    return Commands.waitUntil(() -> nearPosition(position))
        .withName(getName() + " UNTIL " + position);
  }
}
