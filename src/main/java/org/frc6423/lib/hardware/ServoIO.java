// Copyright (c) !!YEAR!! FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.hardware;

import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.units.Unit;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import java.util.function.UnaryOperator;

public abstract class ServoIO implements Sendable {
  public static enum SetpointType {
    IDLE,
    DUTYCYCLE,
    VOLTAGE,
    CURRENT,
    POSITION,
    VELOCITY,
    PROFILED_POSITION,
    PROFILED_VELOCITY;
  }

  public static class Setpoint {
    private final double value;
    private final SetpointType type;
    private final UnaryOperator<ServoIO> applier;

    private Setpoint(double value, SetpointType mode, UnaryOperator<ServoIO> applier) {
      this.value = value;
      this.type = mode;
      this.applier = applier;
    }

    public double getSetpointValue() {
      return value;
    }

    public SetpointType getSetpointType() {
      return type;
    }

    public static Setpoint withDutycycle(double output) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setDutyCycleSetpoint(output);
            return io;
          };
      return new Setpoint(output, SetpointType.DUTYCYCLE, applier);
    }

    public static Setpoint withVoltage(double output) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setVoltageSetpoint(output);
            return io;
          };
      return new Setpoint(output, SetpointType.VOLTAGE, applier);
    }

    public static Setpoint withCurrent(double output) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setCurrentSetpoint(output);
            return io;
          };
      return new Setpoint(output, SetpointType.CURRENT, applier);
    }

    public static Setpoint withPosition(double output) {
      return withPosition(output, 0);
    }

    public static Setpoint withPosition(double output, int slot) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setPositionSetpoint(output, slot);
            return io;
          };
      return new Setpoint(output, SetpointType.POSITION, applier);
    }

    public static Setpoint withVelocity(double output) {
      return withVelocity(output, 0.0, 0);
    }

    public static Setpoint withVelocity(double output, int slot) {
      return withVelocity(output, 0.0, slot);
    }

    public static Setpoint withVelocity(double output, double acceleration) {
      return withVelocity(output, acceleration, 0);
    }

    public static Setpoint withVelocity(double output, double acceleration, int slot) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setVelocitySetpoint(output, acceleration, slot);
            return io;
          };
      return new Setpoint(output, SetpointType.VELOCITY, applier);
    }

    public static Setpoint withProfiledPosition(double output) {
      return withProfiledPosition(output, 0);
    }

    public static Setpoint withProfiledPosition(double output, int slot) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setProfiledPositionSetpoint(output, slot);
            return io;
          };
      return new Setpoint(output, SetpointType.PROFILED_POSITION, applier);
    }

    public static Setpoint withProfiledVelocity(double output) {
      return withProfiledVelocity(output);
    }

    public static Setpoint withProfiledVelocity(double output, int slot) {
      return withProfiledVelocity(output, 0.0, slot);
    }

    public static Setpoint withProfiledVelocity(double output, double acceleration) {
      return withProfiledVelocity(output, acceleration, 0);
    }

    public static Setpoint withProfiledVelocity(double output, double acceleration, int slot) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setProfiledVelocitySetpoint(output, acceleration, slot);
            return io;
          };
      return new Setpoint(output, SetpointType.PROFILED_VELOCITY, applier);
    }

    public static Setpoint idle() {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.idle();
            return io;
          };
      return new Setpoint(0.0, SetpointType.IDLE, applier);
    }

    public void apply(ServoIO io) {
      applier.apply(io);
    }
  }

  private final Unit positionUnit;

  private Setpoint currentSetpoint = Setpoint.idle();

  public ServoIO(Unit positionUnit) {
    this.positionUnit = positionUnit;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Applied Voltage", this::getAppliedVoltage, null);
    builder.addDoubleProperty("Supply Current (Amps)", this::getSupplyCurrentAmperes, null);
    builder.addDoubleProperty("Stator Current (Amps)", this::getStatorCurrentAmperes, null);

    builder.addDoubleProperty(
        "Position (" + getPositionUnit().toString() + ")", this::getPosition, null);
    builder.addDoubleProperty(
        "Velocity (" + getVelocityUnit().toString() + ")", this::getVelocity, null);

    builder.addDoubleProperty("Temperature (Celsius)", this::getTemperatureCelsius, null);

    builder.addStringProperty(
        "Setpoint Type", () -> getCurrentSetpoint().getSetpointType().toString(), null);
    builder.addDoubleProperty("Setpoint", () -> getCurrentSetpoint().getSetpointValue(), null);
  }

  public Unit getPositionUnit() {
    return positionUnit;
  }

  public Unit getVelocityUnit() {
    return positionUnit.per(Seconds);
  }

  public abstract double getAppliedVoltage();

  public abstract double getSupplyCurrentAmperes();

  public abstract double getStatorCurrentAmperes();

  public abstract double getPosition();

  public abstract double getVelocity();

  public abstract double getTemperatureCelsius();

  public Setpoint getCurrentSetpoint() {
    return currentSetpoint;
  }

  protected abstract void idle();

  protected abstract void setDutyCycleSetpoint(double dutycycle);

  protected abstract void setVoltageSetpoint(double voltage);

  protected abstract void setCurrentSetpoint(double current);

  protected abstract void setPositionSetpoint(double position, int slot);

  protected abstract void setVelocitySetpoint(double velocity, double acceleration, int slot);

  protected abstract void setProfiledPositionSetpoint(double position, int slot);

  protected abstract void setProfiledVelocitySetpoint(
      double velocity, double acceleration, int slot);

  public void resetEncoder() {
    resetEncoder(getPosition());
  }

  public abstract void resetEncoder(double position);

  public abstract void setLeaderServo(int canDeviceId, boolean reverseDirection);

  /**
   * Enable/disable brake mode
   *
   * <p>Brake mode affects how the servo slows down when no power is applied:
   *
   * <ul>
   *   <li>Enabled: The servo short its terminals to deaccelerate quicker
   *   <li>Disabled: The servo will apply no form of braking and slowly deaccelerate
   * </ul>
   *
   * By default, brake mode is enabled
   *
   * <p>! WARNING ! When brake is disabled, the servo will slowly deaccelerate which can make it a
   * safety risk
   *
   * @param enabled set true to enable brake, false to disable
   */
  public abstract void brakeEnabled(boolean enabled);
}
