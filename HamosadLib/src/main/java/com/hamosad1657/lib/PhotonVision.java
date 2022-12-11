/**
 * <h1>This is the PhotonVision hamosad implementation. </h1>
 * it is used to retrive data from the photonvision pipeline
 */

package com.hamosad1657.lib;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Transform3d;

public class PhotonVision {
    /// this is the PhotonCamera instance, name it after your camera name in photonvision
    PhotonCamera camera = new PhotonCamera("MicrosoftCamera");
    ///gets the robot's x (x is the further or closer the robot is reletive to the tag)
    public double robotCurrentX() {
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if (hasTargets) {
            PhotonTrackedTarget target = result.getBestTarget();
            Transform3d bestCameraToTarget = target.getBestCameraToTarget();
            return bestCameraToTarget.getX();
        }
        return 0.0;
    }
    ///gets the robot's y (y is the further up or down the robot is reletive to the tag)
    public double robotCurrentY() {
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if (hasTargets) {
            PhotonTrackedTarget target = result.getBestTarget();
            Transform3d bestCameraToTarget = target.getBestCameraToTarget();
            return bestCameraToTarget.getY();
        }
        return 0.0;
    }
    ///gets the robot's z (z is the further left or right the robot is reletive to the tag)
    public double robotCurrentZ() {
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if (hasTargets) {
            PhotonTrackedTarget target = result.getBestTarget();
            Transform3d bestCameraToTarget = target.getBestCameraToTarget();
            return bestCameraToTarget.getZ();
        }
        return 0.0;
    }
}