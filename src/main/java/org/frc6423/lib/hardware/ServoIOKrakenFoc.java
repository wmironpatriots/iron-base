// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.hardware;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.PositionTorqueCurrentFOC;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.units.Unit;

public class ServoIOKrakenFoc extends ServoIO {
  private final TalonFX servo;
  private final TalonFXConfiguration config;

  private final BaseStatusSignal voltSig, supplySig, statorSig, poseSig, velSig, tempSig;

  private final DutyCycleOut dutycycleReq = new DutyCycleOut(0.0).withEnableFOC(true);
  private final VoltageOut voltReq = new VoltageOut(0.0).withEnableFOC(true);
  private final TorqueCurrentFOC currentReq = new TorqueCurrentFOC(0.0);
  private final PositionTorqueCurrentFOC poseReq = new PositionTorqueCurrentFOC(0.0);
  private final VelocityTorqueCurrentFOC velReq = new VelocityTorqueCurrentFOC(0.0);
  private final MotionMagicTorqueCurrentFOC profPoseReq = new MotionMagicTorqueCurrentFOC(0.0);
  private final MotionMagicVelocityTorqueCurrentFOC profVelReq =
      new MotionMagicVelocityTorqueCurrentFOC(0.0);

  public ServoIOKrakenFoc(
      Unit unit, String canBusId, int canDeviceId, TalonFXConfiguration config) {
    super(unit);
    servo = new TalonFX(canDeviceId, canBusId);
    this.config = config;

    voltSig = servo.getMotorVoltage();
    supplySig = servo.getSupplyCurrent();
    statorSig = servo.getStatorCurrent();
    poseSig = servo.getPosition();
    velSig = servo.getVelocity();
    tempSig = servo.getDeviceTemp();
  }

  @Override
  public double getAppliedVoltage() {
    return voltSig.getValueAsDouble();
  }

  @Override
  public double getSupplyCurrentAmperes() {
    return supplySig.getValueAsDouble();
  }

  @Override
  public double getStatorCurrentAmperes() {
    return statorSig.getValueAsDouble();
  }

  @Override
  public double getPosition() {
    return poseSig.getValueAsDouble();
  }

  @Override
  public double getVelocity() {
    return velSig.getValueAsDouble();
  }

  @Override
  public double getTemperatureCelsius() {
    return tempSig.getValueAsDouble();
  }

  @Override
  protected void idle() {
    servo.stopMotor();
  }

  @Override
  protected void setDutyCycleSetpoint(double dutycycle) {
    servo.setControl(dutycycleReq.withOutput(dutycycle));
  }

  @Override
  protected void setVoltageSetpoint(double voltage) {
    servo.setControl(voltReq.withOutput(voltage));
  }

  @Override
  protected void setCurrentSetpoint(double current) {
    servo.setControl(currentReq.withOutput(current));
  }

  @Override
  protected void setPositionSetpoint(double position, int slot) {
    servo.setControl(poseReq.withPosition(position).withSlot(slot));
  }

  @Override
  protected void setVelocitySetpoint(double velocity, double acceleration, int slot) {
    servo.setControl(velReq.withVelocity(velocity).withAcceleration(acceleration).withSlot(slot));
  }

  @Override
  protected void setProfiledPositionSetpoint(double position, int slot) {
    servo.setControl(profPoseReq.withPosition(position).withSlot(slot));
  }

  @Override
  protected void setProfiledVelocitySetpoint(double velocity, double acceleration, int slot) {
    servo.setControl(
        profVelReq.withVelocity(velocity).withAcceleration(acceleration).withSlot(slot));
  }

  @Override
  public void resetEncoder(double position) {
    servo.setPosition(position);
  }

  @Override
  public void setLeaderServo(int canDeviceId, boolean reverseDirection) {
    servo.setControl(new Follower(canDeviceId, reverseDirection));
  }

  @Override
  public void brakeEnabled(boolean enabled) {
    config.MotorOutput.NeutralMode = enabled ? NeutralModeValue.Brake : NeutralModeValue.Coast;
  }
}
