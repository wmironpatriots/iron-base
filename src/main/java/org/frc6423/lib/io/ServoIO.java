// TODO PUT COPYRIGHT YEAR!!! Copyright (c) <YEAR> FRC 6423 - Ward Melville Iron Patriots
// https://github.com/wmironpatriots
// 
// Open Source Software; you can modify and/or share it under the terms of
// MIT license file in the root directory of this project

package org.frc6423.lib.io;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.TimeUnit;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import java.util.function.UnaryOperator;

// TODO go through javadoc again
/** Generialized hardware methods for a servo */
public abstract class ServoIO implements Sendable {
  /** Represents the available control modes for a servo */
  public static enum ControlMode {
    /** Servo has no applied output */
    IDLE,
    /** Servo is directly controlled by specified voltage output */
    VOLTAGE,
    /**
     * Servo is directly controlled by specified dutycycle ratio (pulse width over signal period)
     */
    DUTYCYCLE,
    /** Servo is moving to or holding a specified position using a feedback loop */
    POSITION,
    /**
     * Servo is moving to or holding a specified position utilizing motion profiled feedback loop
     */
    MOTION_PROFILED,
    /** Servo is accelerating to or holding a specified speed using a feedback loop */
    VELOCITY;
  }

  /** Represents a specified setpoint of a specified {@link ControlMode} for a servo */
  public static class ControlRequest {
    private final double value;
    private final ControlMode mode;
    private final UnaryOperator<ServoIO> applier;

    private ControlRequest(double value, ControlMode mode, UnaryOperator<ServoIO> applier) {
      this.value = value;
      this.mode = mode;
      this.applier = applier;
    }

    /**
     * @return return request setpoint value
     */
    public double getValue() {
      return value;
    }

    /**
     * @return {@link ControlMode} representing the type of setpoint of the request
     */
    public ControlMode getControlMode() {
      return mode;
    }

    /**
     * Create a new voltage {@link ControlRequest}
     *
     * @param output desired voltage output
     * @return {@link ControlRequest}
     */
    public static ControlRequest withVoltage(double output) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setVoltageSetpoint(output);
            return io;
          };
      return new ControlRequest(output, ControlMode.VOLTAGE, applier);
    }

    /**
     * Create a new dutycycle {@link ControlRequest}
     *
     * @param output desired dutycycle output (pulse width over signal period)
     * @return {@link ControlRequest}
     */
    public static ControlRequest withDutyCycle(double output) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setDutyCycleSetpoint(output);
            return io;
          };
      return new ControlRequest(output, ControlMode.DUTYCYCLE, applier);
    }

    /**
     * Create a new position {@link ControlRequest}
     *
     * @param output desired position output (in subsystem's position unit)
     * @return {@link ControlRequest}
     */
    public static ControlRequest withPosition(double output) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setPositionSetpoint(output);
            return io;
          };
      return new ControlRequest(output, ControlMode.POSITION, applier);
    }

    /**
     * Create a new motion-profiled {@link ControlRequest}
     *
     * @param output desired position output (in subsystem's position unit)
     * @param velocity velocity limit (in subsystem's velocity unit)
     * @param acceleration acceleration limit (in subsystem's acceleration unit)
     * @param jerk jerk limit (in subsystem's jerk unit)
     * @return {@link ControlRequest}
     */
    public static ControlRequest withMotionProfilingSetpoint(
        double output, double velocity, double acceleration, double jerk) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setMotionProfilingSetpoint(output, velocity, acceleration, jerk);
            return io;
          };
      return new ControlRequest(output, ControlMode.MOTION_PROFILED, applier);
    }

    /**
     * Create a new velocity (@link ControlRequest)
     *
     * @param output desired output velocity (in subsystem's velocity unit)
     * @param acceleration acceleration limit
     * @return {@link ControlRequest}
     */
    public static ControlRequest withVelocity(double output, double acceleration) {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.setVelocitySetpoint(output, acceleration);
            return io;
          };
      return new ControlRequest(output, ControlMode.VELOCITY, applier);
    }

    /**
     * Create a new velocity {@link ControlRequest}
     *
     * @param output desired output velocity (in subsystem's velocity unit)
     * @return {@link ControlRequest}
     */
    public static ControlRequest withVelocity(double output) {
      return withVelocity(output, 0.0);
    }

    /**
     * Create a new idle {@link ControlRequest}
     *
     * @return {@link ControlRequest}
     */
    public static ControlRequest idle() {
      UnaryOperator<ServoIO> applier =
          (ServoIO io) -> {
            io.idle();
            return io;
          };
      return new ControlRequest(0.0, ControlMode.IDLE, applier);
    }

    /**
     * Run request on a servo
     *
     * @param io {@link ServoIO} to run request on
     */
    public void apply(ServoIO io) {
      applier.apply(io);
    }
  }

  public final AngleUnit positionUnit;
  public final TimeUnit timeUnit;

  public ControlRequest currentRequest;
  public boolean enabled = false;

  public ServoIO(AngleUnit positionUnit, TimeUnit timeUnit) {
    this.positionUnit = positionUnit;
    this.timeUnit = timeUnit;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Applied Voltage", this::getAppliedVoltage, null);
    builder.addDoubleProperty("Supply Current (Amps)", this::getSupplyCurrentAmperes, null);
    builder.addDoubleProperty("Stator Current (Amps)", this::getStatorCurrentAmperes, null);
    builder.addDoubleProperty("Position (" + positionUnit.name() + ")", this::getPosition, null);
    builder.addDoubleProperty(
        "Velocity (" + positionUnit.per(timeUnit) + ")", this::getVelocity, null);
    builder.addDoubleProperty("Temperature (Celsius)", this::getTemperatureCelsius, null);
  }

  /**
   * @return applied voltage
   */
  public abstract double getAppliedVoltage();

  /**
   * @return supply current (in amperes)
   */
  public abstract double getSupplyCurrentAmperes();

  /**
   * @return stator current (in amperes)
   */
  public abstract double getStatorCurrentAmperes();

  /**
   * @return position (in subsystem position unit)
   */
  public abstract double getPosition();

  /**
   * @return velocity (in subsystem velocity unit)
   */
  public abstract double getVelocity();

  /**
   * @return temperature (in celsius)
   */
  public abstract double getTemperatureCelsius();

  /**
   * Set dutycyle setpoint
   *
   * @param dutyCycle desired dutycycle output (pulse width over signal period)
   */
  protected abstract void setDutyCycleSetpoint(double dutyCycle);

  /**
   * Set voltage setpoint
   *
   * @param volts desired voltage output
   */
  protected abstract void setVoltageSetpoint(double volts);

  /**
   * Set position setpoint
   *
   * @param position desired position output (in subsystem position unit)
   */
  protected abstract void setPositionSetpoint(double position);

  /**
   * Set motion profiled setpoint /w current constraints
   *
   * @param position desired position output (in subsystem position unit)
   */
  protected abstract void setMotionProfilingSetpoint(double position);

  /**
   * Set motion profiled setpoint /w new constraints
   *
   * @param position desired position output (in subsystem position unit)
   * @param velocity velocity limit (in subsystem velocity unit)
   * @param acceleration acceleration limit (in subsystem acceleration unit)
   * @param jerk jerk limit (in subsystem jerk unit)
   */
  protected abstract void setMotionProfilingSetpoint(
      double position, double velocity, double acceleration, double jerk);

  /**
   * Set velocity setpoint
   *
   * @param velocity desired velocity output (in subsystem velocity unit)
   */
  protected abstract void setVelocitySetpoint(double velocity);

  /**
   * Set velocity setpoint /w acceleration limit
   *
   * @param velocity desired velocity output (in subsystem velocity unit)
   * @param acceleration acceleration limit (in subsystem acceleration unit)
   */
  protected abstract void setVelocitySetpoint(double velocity, double acceleration);

  /**
   * Stop all output
   *
   * <p>If coasting, servo will spin down else, servo will apply brake
   */
  protected abstract void idle();

  /**
   * Set current {@link ControlRequest} if enabled
   *
   * @param desiredRequest desired {@link ControlRequest}
   */
  public void applyRequest(ControlRequest desiredRequest) {
    if (enabled) {
      currentRequest = desiredRequest;
      currentRequest.apply(this);
    }
  }

  /** Enable servo; {@link ControlRequest}s will be applied */
  public void enable() {
    enabled = true;
  }

  /** Disable servo; idle {@link ControlRequest} will be automatically applied */
  public void disable() {
    enabled = false;
    ControlRequest.idle().apply(this);
  }

  /**
   * Reset internal relative encoder to position
   *
   * @param position position to reset to (in subsystem position unit)
   */
  public abstract void resetEncoder(double position);

  /**
   * Set hard limits
   *
   * @param reverseLimit reverse hard limit
   * @param forwardLimit forward hard limit
   */
  public abstract void setHardLimits(double reverseLimit, double forwardLimit);

  /**
   * Set soft limits
   *
   * @param reverseLimit reverse soft limit
   * @param forwardLimit forward soft limit
   */
  public abstract void setSoftLimits(double reverseLimit, double forwardLimit);

  /**
   * Set leader motor to follow
   *
   * @param CanBusId name of the CAN bus containing the leader servo
   * @param CanId CAN ID of leader servo
   * @param reverseDirection on true follower will run requests in the reverse direction
   */
  public abstract void setLeaderServo(String CanBusId, int CanId, boolean reverseDirection);

  /**
   * Set gain values
   *
   * @param kG gravity feedforward constant
   * @param kS static friction feedforward constant
   * @param kV velocity feedforward constant
   * @param kA acceleration feedforward constant
   * @param kP proportional feedback constant
   * @param kI integral feedback constant
   * @param kD derivative feedback constant
   */
  public abstract void setGains(
      double kG, double kS, double kV, double kA, double kP, double kI, double kD);

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
