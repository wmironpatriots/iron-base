// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.driver;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.Supplier;
import org.frc6423.lib.hardware.ServoIO;
import org.frc6423.lib.hardware.ServoIO.Setpoint;

@Logged
public class MotorSubsystem extends SubsystemBase {
  protected final ServoIO hardware;

  public MotorSubsystem(ServoIO hardware) {
    this.hardware = hardware;
  }

  @Override
  public void periodic() {}

  public double getAppliedVoltage() {
    return hardware.getAppliedVoltage();
  }

  public double getSupplyCurrentAmperes() {
    return hardware.getSupplyCurrentAmperes();
  }

  public double getStatorCurrentAmperes() {
    return hardware.getStatorCurrentAmperes();
  }

  public double getPosition() {
    return hardware.getPosition();
  }

  public double getVelocity() {
    return hardware.getVelocity();
  }

  public double getTemperatureCelsius() {
    return hardware.getTemperatureCelsius();
  }

  public Setpoint getCurrentSetpoint() {
    return hardware.getCurrentSetpoint();
  }

  protected void applySetpoint(Setpoint setpoint) {
    setpoint.apply(hardware);
  }

  protected void setPosition(double position) {
    hardware.resetEncoder(position);
  }

  public Command setSetpointCmd(Setpoint setpoint) {
    return runOnce(() -> applySetpoint(setpoint))
        .withName(
            getName() + " SET " + setpoint.getSetpointType() + " " + setpoint.getSetpointValue());
  }

  public Command runSetpointCmd(Supplier<Setpoint> setpoint) {
    return run(() -> applySetpoint(setpoint.get()))
        .withName(
            getName()
                + " RUN "
                + setpoint.get().getSetpointValue()
                + " "
                + setpoint.get().getSetpointValue());
  }
}
