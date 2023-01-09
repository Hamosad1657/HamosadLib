
package com.hamosad1657.lib.sensors;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderSimCollection;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.hamosad1657.lib.HaUnitConvertor;

import edu.wpi.first.math.geometry.Rotation2d;

public class HaCANCoder {
    private final double kCANCoderTicksPerRev = 4096;
    private final CANCoder cancoder;
    private final CANCoderSimCollection simCANCoder;
    private double simAngleDeg = 0;
    private double simVelocityDegPS = 0;

    public HaCANCoder(int CANCoderID, double offsetDegrees) {
        this.cancoder = new CANCoder(CANCoderID);
        this.cancoder.configMagnetOffset(offsetDegrees);
        this.cancoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
        this.cancoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);

        this.simCANCoder = this.cancoder.getSimCollection();
    }

    public double getAbsAngleDeg() {
        return this.cancoder.getAbsolutePosition();
    }
    public double getAbsAngleRad() {
        return HaUnitConvertor.degToRad(this.getAbsAngleDeg());
    }
    public Rotation2d getAbsAngleRotation2d() {
        return Rotation2d.fromDegrees(this.getAbsAngleDeg());
    }

    public double getVelocityDegPS() {
        return this.cancoder.getVelocity();
    }
    public double getVelocityRadPS() {
        return HaUnitConvertor.degPSToRadPS(this.cancoder.getVelocity());
    }
    public double getVelocityMPS(double wheelRadiusM) {
        return HaUnitConvertor.degPSToMPS(this.cancoder.getVelocity(), wheelRadiusM);
    }

    public double getVelocityRPM() {
        return HaUnitConvertor.degPSToRPM(this.cancoder.getVelocity());
    }


    // Sim CANCoder methods

    public double getSimAngleDeg() {
        return this.simAngleDeg;
    }
    public double getSimAngleRad() {
        return HaUnitConvertor.degToRad(this.simAngleDeg);
    }
    public Rotation2d getSimAngleRotation2d() {
        return Rotation2d.fromDegrees(this.simAngleDeg);
    }

    public double getSimVelocityDegPS() {
        return this.simVelocityDegPS;
    }
    public double getSimVelocityRadPS() {
        return HaUnitConvertor.degPSToRadPS(this.simVelocityDegPS);
    }
    public double getSimVelocityMPS(double wheelRadiusM) {
        return HaUnitConvertor.degPSToMPS(this.simVelocityDegPS, wheelRadiusM);
    }

    public double getSimVelocityRPM() {
        return HaUnitConvertor.degPSToRPM(this.simVelocityDegPS);
    }

    public void setSimAngleDeg(double angleDeg) {
        this.simAngleDeg = angleDeg;
        this.simCANCoder.setRawPosition((int)this.degreesToCANCoderTicks(simAngleDeg));
    }
    public void setSimAngleRad(double angleRad) {
        this.simAngleDeg = HaUnitConvertor.degToRad(angleRad);
        this.simCANCoder.setRawPosition((int)this.degreesToCANCoderTicks(simAngleDeg));
    }
    public void setSimAngleRotation2d(Rotation2d angleRotation2d) {
        this.simAngleDeg = angleRotation2d.getDegrees();
        this.simCANCoder.setRawPosition((int)this.degreesToCANCoderTicks(simAngleDeg));
    }

    public void setSimVelocityDegPS(double degPS) {
        this.simVelocityDegPS = degPS;
        this.simCANCoder.setVelocity((int)this.degPSToCANCoderTicksPer100MS(this.simVelocityDegPS));
    }

    public void setSimVelocityRadPS(double radPS) {
        this.simVelocityDegPS = HaUnitConvertor.radPSToDegPS(radPS);
        this.simCANCoder.setVelocity((int)this.degPSToCANCoderTicksPer100MS(this.simVelocityDegPS));
    }
    public void setSimVelocityMPS(double MPS, double wheelRadiusM) {
        this.simVelocityDegPS = HaUnitConvertor.MPSToDegPS(MPS, wheelRadiusM);
        this.simCANCoder.setVelocity((int)this.degPSToCANCoderTicksPer100MS(this.simVelocityDegPS));
    }

    public void setSimVelocityRPM(double RPM) {
        this.simVelocityDegPS = HaUnitConvertor.RPMToDegPS(RPM);
        this.simCANCoder.setVelocity((int)this.degPSToCANCoderTicksPer100MS(this.simVelocityDegPS));
    }


    // Private unit convertors

    private double degreesToCANCoderTicks(double degrees) {
        return (degrees / 360.0) * this.kCANCoderTicksPerRev;
    }

    private double degPSToCANCoderTicksPer100MS(double degPS) {
        return (HaUnitConvertor.degPSToRPM(degPS) / 10) * this.kCANCoderTicksPerRev;
    }
}
