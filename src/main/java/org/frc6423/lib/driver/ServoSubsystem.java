// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.driver;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;
import org.frc6423.lib.hardware.ServoIO;
import org.frc6423.lib.hardware.ServoIO.Setpoint;

@Logged
public class ServoSubsystem extends SubsystemBase {
  protected final ServoIO hardware;

  public ServoSubsystem(ServoIO hardware) {
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

  protected Command runDutycycleCmmd(double dutycycle) {
    return runDutycycleCmmd(() -> dutycycle);
  }

  protected Command runDutycycleCmmd(DoubleSupplier dutycycle) {
    return runEnd(
            () -> Setpoint.withDutycycle(dutycycle.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Dutycycle Control");
  }

  protected Command setDutycycleSetpointCmmd(double dutycycle) {
    return setDutycycleSetpointCmmd(() -> dutycycle);
  }

  protected Command setDutycycleSetpointCmmd(DoubleSupplier dutycycle) {
    return run(() -> Setpoint.withDutycycle(dutycycle.getAsDouble()).apply(hardware))
        .withName(getName() + " Set DutyCycle Setpoint");
  }

  protected Command runVoltageCmmd(double voltage) {
    return runVoltageCmmd(() -> voltage);
  }

  protected Command runVoltageCmmd(DoubleSupplier voltage) {
    return runEnd(
            () -> Setpoint.withVoltage(voltage.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Voltage Control");
  }

  protected Command setVoltageSetpointCmmd(double voltage) {
    return setVoltageSetpointCmmd(() -> voltage);
  }

  protected Command setVoltageSetpointCmmd(DoubleSupplier voltage) {
    return run(() -> Setpoint.withVoltage(voltage.getAsDouble()).apply(hardware))
        .withName(getName() + " Set Voltage Setpoint");
  }

  protected Command runCurrentCmmd(double current) {
    return runCurrentCmmd(() -> current);
  }

  protected Command runCurrentCmmd(DoubleSupplier current) {
    return runEnd(
            () -> Setpoint.withCurrent(current.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Current Control");
  }

  protected Command setCurrentSetpointCmmd(double current) {
    return setCurrentSetpointCmmd(() -> current);
  }

  protected Command setCurrentSetpointCmmd(DoubleSupplier current) {
    return run(() -> Setpoint.withCurrent(current.getAsDouble()).apply(hardware))
        .withName(getName() + " Set Current Setpoint");
  }

  protected Command runToPositionCmmd(double position) {
    return runToPositionCmmd(() -> position);
  }

  protected Command runToPositionCmmd(DoubleSupplier position) {
    return runEnd(
            () -> Setpoint.withPosition(position.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Position Control");
  }

  protected Command setPositionSetpointCmmd(double position) {
    return setPositionSetpointCmmd(() -> position);
  }

  protected Command setPositionSetpointCmmd(DoubleSupplier position) {
    return run(() -> Setpoint.withPosition(position.getAsDouble()).apply(hardware))
        .withName(getName() + " Set Position Setpoint");
  }

  protected Command setPositionSetpointBlockingCmmd(double position, double epsilon) {
    return setPositionSetpointBlockingCmmd(() -> position, () -> epsilon);
  }

  protected Command setPositionSetpointBlockingCmmd(
      DoubleSupplier position, DoubleSupplier epsilon) {
    return setPositionSetpointCmmd(position)
        .until(() -> MathUtil.isNear(position.getAsDouble(), getPosition(), epsilon.getAsDouble()))
        .withName(getName() + " Set Position Setpoint Blocking");
  }

  protected Command runToVelocityCmmd(double velocity) {
    return runToVelocityCmmd(() -> velocity);
  }

  protected Command runToVelocityCmmd(DoubleSupplier velocity) {
    return runEnd(
            () -> Setpoint.withVelocity(velocity.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Velocity Control");
  }

  protected Command setVelocitySetpointCmmd(double velocity) {
    return setVelocitySetpointCmmd(() -> velocity);
  }

  protected Command setVelocitySetpointCmmd(DoubleSupplier velocity) {
    return run(() -> Setpoint.withVelocity(velocity.getAsDouble()).apply(hardware))
        .withName(getName() + " Set Velocity Setpoint");
  }

  protected Command setVelocitySetpointBlockingCmmd(double velocity, double epsilon) {
    return setVelocitySetpointBlockingCmmd(() -> velocity, () -> epsilon);
  }

  protected Command setVelocitySetpointBlockingCmmd(
      DoubleSupplier velocity, DoubleSupplier epsilon) {
    return setVelocitySetpointCmmd(velocity)
        .until(() -> MathUtil.isNear(velocity.getAsDouble(), getVelocity(), epsilon.getAsDouble()))
        .withName(getName() + " Set Velocity Setpoint Blocking");
  }

  protected Command runToProfiledPositionCmmd(double position) {
    return runToProfiledPositionCmmd(() -> position);
  }

  protected Command runToProfiledPositionCmmd(DoubleSupplier position) {
    return runEnd(
            () -> Setpoint.withProfiledPosition(position.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Profiled Position Control");
  }

  protected Command setProfiledPositionSetpointCmmd(double position) {
    return setProfiledPositionSetpointCmmd(() -> position);
  }

  protected Command setProfiledPositionSetpointCmmd(DoubleSupplier position) {
    return run(() -> Setpoint.withProfiledPosition(position.getAsDouble()).apply(hardware))
        .withName(getName() + " Set Profiled Position Setpoint");
  }

  protected Command setProfiledPositionSetpointBlockingCmmd(double position, double epsilon) {
    return setProfiledPositionSetpointBlockingCmmd(() -> position, () -> epsilon);
  }

  protected Command setProfiledPositionSetpointBlockingCmmd(
      DoubleSupplier position, DoubleSupplier epsilon) {
    return setProfiledPositionSetpointCmmd(position)
        .until(() -> MathUtil.isNear(position.getAsDouble(), getPosition(), epsilon.getAsDouble()))
        .withName(getName() + " Set Profiled Position Setpoint Blocking");
  }

  protected Command runToProfiledVelocityCmmd(double velocity) {
    return runToProfiledVelocityCmmd(() -> velocity);
  }

  protected Command runToProfiledVelocityCmmd(DoubleSupplier velocity) {
    return runEnd(
            () -> Setpoint.withProfiledVelocity(velocity.getAsDouble()).apply(hardware),
            () -> Setpoint.idle().apply(hardware))
        .withName(getName() + " Run Profiled Velocity Control");
  }

  protected Command setProfiledVelocitySetpointCmmd(double velocity) {
    return setProfiledVelocitySetpointCmmd(() -> velocity);
  }

  protected Command setProfiledVelocitySetpointCmmd(DoubleSupplier velocity) {
    return run(() -> Setpoint.withProfiledVelocity(velocity.getAsDouble()).apply(hardware))
        .withName(getName() + " Set Profiled Velocity Setpoint");
  }

  protected Command setProfiledVelocitySetpointBlockingCmmd(double velocity, double epsilon) {
    return setProfiledVelocitySetpointBlockingCmmd(() -> velocity, () -> epsilon);
  }

  protected Command setProfiledVelocitySetpointBlockingCmmd(
      DoubleSupplier velocity, DoubleSupplier epsilon) {
    return setProfiledVelocitySetpointCmmd(velocity)
        .until(() -> MathUtil.isNear(velocity.getAsDouble(), getVelocity(), epsilon.getAsDouble()))
        .withName(getName() + " Set Profiled Velocity Setpoint Blocking");
  }
}
