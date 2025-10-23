// TODO PUT COPYRIGHT YEAR!!! Copyright (c) <YEAR> FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.io;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicTorqueCurrentFOC;
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.PositionTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ServoIOTalonFx extends ServoIO {
  private final TalonFX motor;
  private final TalonFXConfiguration config;

  private final BaseStatusSignal voltSig, supCurrentSig, statCurrentSig, poseSig, velSig, tempSig;

  private final DutyCycleOut dutyCycleReq = new DutyCycleOut(0.0);
  private final VoltageOut voltReq = new VoltageOut(0.0);
  private final PositionTorqueCurrentFOC poseReq = new PositionTorqueCurrentFOC(0.0);
  private final VelocityTorqueCurrentFOC velReq = new VelocityTorqueCurrentFOC(0.0);
  private final MotionMagicTorqueCurrentFOC profiledPoseReq = new MotionMagicTorqueCurrentFOC(0.0);
  private final MotionMagicVelocityTorqueCurrentFOC profiledVelReq =
      new MotionMagicVelocityTorqueCurrentFOC(0.0);

  public ServoIOTalonFx(int deviceId, String canBusId, TalonFXConfiguration config) {
    motor = new TalonFX(deviceId, canBusId);
    this.config = config;

    motor.getConfigurator().apply(config);

    voltSig = motor.getMotorVoltage(true);
    supCurrentSig = motor.getSupplyCurrent(true);
    statCurrentSig = motor.getStatorCurrent(true);
    poseSig = motor.getPosition(true);
    velSig = motor.getVelocity(true);
    tempSig = motor.getDeviceTemp(true);
  }

  @Override
  public double getAppliedVoltage() {
    return voltSig.getValueAsDouble();
  }

  @Override
  public double getSupplyCurrentAmperes() {
    return supCurrentSig.getValueAsDouble();
  }

  @Override
  public double getStatorCurrentAmperes() {
    return statCurrentSig.getValueAsDouble();
  }

  @Override
  public double getPositionRevs() {
    return poseSig.getValueAsDouble();
  }

  @Override
  public double getVelocityRps() {
    return velSig.getValueAsDouble();
  }

  @Override
  public double getTemperatureCelsius() {
    return tempSig.getValueAsDouble();
  }

  @Override
  protected void idle() {
    motor.stopMotor();
  }

  @Override
  protected void setDutyCycleSetpoint(double dutycycle) {
    motor.setControl(dutyCycleReq.withOutput(dutycycle).withEnableFOC(true));
  }

  @Override
  protected void setVoltageSetpoint(double voltage) {
    motor.setControl(voltReq.withOutput(voltage).withEnableFOC(true));
  }

  @Override
  protected void setPositionSetpoint(double position) {
    motor.setControl(poseReq.withPosition(position));
  }

  @Override
  protected void setVelocitySetpoint(double velocity) {
    motor.setControl(velReq.withVelocity(velocity));
  }

  @Override
  protected void setVelocitySetpoint(double velocity, double acceleration) {
    motor.setControl(velReq.withVelocity(velocity).withAcceleration(acceleration));
  }

  @Override
  protected void setProfiledPositionSetpoint(double position) {
    motor.setControl(profiledPoseReq.withPosition(position));
  }

  @Override
  protected void setProfiledVelocitySetpoint(double velocity) {
    motor.setControl(profiledVelReq.withVelocity(velocity));
  }

  @Override
  public void resetEncoder(double position) {
    motor.setPosition(position);
  }

  @Override
  public void setHardLimits(double reverseLimit, double forwardLimit) {
    config.HardwareLimitSwitch.ReverseLimitEnable = true;
    config.HardwareLimitSwitch.ReverseLimitAutosetPositionValue = reverseLimit;

    config.HardwareLimitSwitch.ForwardLimitEnable = true;
    config.HardwareLimitSwitch.ForwardLimitAutosetPositionValue = forwardLimit;

    motor.getConfigurator().apply(config);
  }

  @Override
  public void setLeaderServo(int canDeviceId, boolean reverseDirection) {
    motor.setControl(new Follower(canDeviceId, reverseDirection));
  }

  @Override
  public void brakeEnabled(boolean enabled) {
    config.MotorOutput.NeutralMode = enabled ? NeutralModeValue.Brake : NeutralModeValue.Coast;

    motor.getConfigurator().apply(config);
  }
}
