package com.hamosad1657.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
    private static NetworkTable table;
    private static ShuffleboardTab tab;
    private static NetworkTableEntry tv, tx, ty, ta;
    private static NetworkTableEntry tvEntry, txEntry, tyEntry, taEntry;

    public static void initialize() {
        NetworkTableInstance instance = NetworkTableInstance.getDefault();
        table = instance.getTable("limelight");
        tab = Shuffleboard.getTab("LimeLight");
        tv = table.getEntry("tv");
        tx = table.getEntry("tx");
        ty = table.getEntry("ty");
        ta = table.getEntry("ta");
        tvEntry = tab.add("V", tv.getDouble(RobotConstants.LimeLightConstants.kDefaultValue)).getEntry();
        txEntry = tab.add("X", tx.getDouble(RobotConstants.LimeLightConstants.kDefaultValue)).getEntry();
        tyEntry = tab.add("Y", ty.getDouble(RobotConstants.LimeLightConstants.kDefaultValue)).getEntry();
        taEntry = tab.add("A", ta.getDouble(RobotConstants.LimeLightConstants.kDefaultValue)).getEntry();
    }

    /// Horizontal offset from crosshair to target's center: -29.8 to 29.8
    public static double getTx() {
        return tx.getDouble(RobotConstants.LimeLightConstants.kDefaultValue);
    }

    /// Vertical offset from crosshair to target's center: -24.85 to 24.85
    public static double getTy() {
        return ty.getDouble(RobotConstants.LimeLightConstants.kDefaultValue);
    }

    /// Target area: 0% to 100% of the frame
    public static double getTa() {
        return ta.getDouble(RobotConstants.LimeLightConstants.kDefaultValue);
    }

    /// Is there a valid target: 0 or 1
    public static double getTv() {
        return tv.getDouble(RobotConstants.LimeLightConstants.kDefaultValue);
    }

    public void updateShuffleboardValues() {
        tvEntry.setDouble(getTv());
        tyEntry.setDouble(getTy());
        txEntry.setDouble(getTx());
        taEntry.setDouble(getTa());
        distanceEntry.setDouble(getDistance());
    }
}
