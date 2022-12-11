/**
 * <h1>This is the PhotonVision hamosad implementation.</h1>
 * It is used to retrive data from the PhotonVision pipeline.
 */

package com.hamosad1657.lib;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Transform3d;

/// Retrieves the robot's position from PhotonVision's pipeline.
public class PhotonVision {
    /// This is the PhotonCamera instance, name it after your camera name in photonvision.
    PhotonCamera camera = new PhotonCamera("MicrosoftCamera");

    // Get the robot's x, y, and z.
    public Transform3d getRobotTransform3D() {
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if (hasTargets) {
            PhotonTrackedTarget target = result.getBestTarget();
           return target.getBestCameraToTarget();
        }
        return null;
    }

    /// Gets the robot's x (x is the further or closer the robot is reletive to the tag).
    public Double robotCurrentX() {
        var transform = this.getRobotTransform3D();
        if (transform != null) {
            return transform.getX();
        }
        return null;
    }

    /// Gets the robot's y (y is the further up or down the robot is reletive to the tag).
    public Double robotCurrentY() {
        var transform = this.getRobotTransform3D();
        if (transform != null) {
            return transform.getY();
        }
        return null;
    }

    /// Gets the robot's z (z is the further left or right the robot is reletive to the tag).
    public Double robotCurrentZ() {
        var transform = this.getRobotTransform3D();
        if (transform != null) {
            return transform.getZ();
        }
        return null;
    }
}