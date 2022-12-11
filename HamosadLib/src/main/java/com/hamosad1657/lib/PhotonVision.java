/**
 * <h1>This is the PhotonVision hamosad implementation.</h1>
 * It is used to retrive data from the PhotonVision pipeline.
 */

package com.hamosad1657.lib;

import java.lang.annotation.Target;
import java.util.HashMap;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;

public class PhotonVision {

    /**
     * The TagHashMap is a hashmap with the tag's ID as the key and its positin on the field.
     * We use this hashmap to calculate a pose for the robot itself 
     * i.e (robot x to tag + tag's actual x = robot's actual x, and so on...)
     */
    HashMap<Integer, Pose3d> TagHashMap = new HashMap<Integer, Pose3d>();

    /// This is the PhotonCamera instance, name it after your camera name in photonvision.
    PhotonCamera camera = new PhotonCamera("MicrosoftCamera");

    /// Gets the best target from PhotonVision.
    public PhotonTrackedTarget getTagBestTarget() {
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if (hasTargets) {
            PhotonTrackedTarget target = result.getBestTarget();
            return target;
        }
        return null;
    }

    /// Gets the best target's Transform3d
    public Transform3d getTagTransform3D() {
        PhotonTrackedTarget target = getTagBestTarget();
        if (target != null) {
            return target.getBestCameraToTarget();
        }
        return null;
    }

    /// Gets the robot's x (x is the further or closer the robot is reletive to the tag).
    public Double getRobotCurrentX() {
        var transform = this.getTagTransform3D();
        if (transform != null) {
            return transform.getX();
        }
        return null;
    }

    /// Gets the robot's y (y is the further up or down the robot is reletive to the tag).
    public Double getRobotCurrentY() {
        var transform = this.getTagTransform3D();
        if (transform != null) {
            return transform.getY();
        }
        return null;
    }

    /// Gets the robot's z (z is the further left or right the robot is reletive to the tag).
    public Double getRobotCurrentZ() {
        var transform = this.getTagTransform3D();
        if (transform != null) {
            return transform.getZ();
        }
        return null;
    }

    /// Gets the robots actual position.
    public Pose3d getRobotpose3D() {
        
        /// Gets tag's position
        PhotonTrackedTarget target = getTagBestTarget();
        int TagID = target.getFiducialId();
        Pose3d tagPose3d = TagHashMap.get(TagID);

        var transform = this.getTagTransform3D();
        if (transform != null) {
            Pose3d RobotPose3d = tagPose3d.transformBy(transform);
            return RobotPose3d;
        }
        return null;
    }
}