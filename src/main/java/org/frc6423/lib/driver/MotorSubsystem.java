// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.driver;

import static edu.wpi.first.units.Units.Revolutions;
import static edu.wpi.first.units.Units.RevolutionsPerSecond;
import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.units.BaseUnits;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
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

  public Voltage getAppliedVoltage() {
    return hardware.getAppliedVoltage();
  }

  public Current getSupplyCurrentAmperes() {
    return hardware.getSupplyCurrent();
  }

  public Current getStatorCurrentAmperes() {
    return hardware.getStatorCurrent();
  }

  public Angle getPosition() {
    return hardware.getPosition();
  }

  public AngularVelocity getVelocity() {
    return hardware.getVelocity();
  }

  public Temperature getTemperatureCelsius() {
    return hardware.getTemperature();
  }

  public Setpoint getSetpoint() {
    return hardware.getCurrentSetpoint();
  }

  public double getSetpointValue() {
    return getSetpoint().getSetpointValue();
  }

  public Angle getSetpointAngle() {
    return getSetpoint().isPositionSetpoint()
        ? Revolutions.of(0)
        : BaseUnits.AngleUnit.of(getSetpointValue());
  }

  public AngularVelocity getSetpointVelocity() {
    return getSetpoint().isVelocitySetpoint()
        ? RevolutionsPerSecond.of(0)
        : BaseUnits.AngleUnit.per(Seconds).of(getSetpointValue());
  }

  protected void applySetpoint(Setpoint setpoint) {
    setpoint.apply(hardware);
  }

  protected void setPosition(Angle position) {
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
