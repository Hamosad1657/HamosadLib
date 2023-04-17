/**
 * <h1>This is the PhotonVision hamosad implementation.</h1>
 * It is used to retrive data from the PhotonVision pipeline.
 */

package com.hamosad1657.lib.vision;

import java.util.HashMap;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public class PhotonVision implements Sendable {

	/**
	 * The TagHashMap is a hashmap with the tag's ID as the key and its positin on the field. We use this hashmap to
	 * calculate a pose for the robot itself i.e (robot x to tag + tag's actual x = robot's actual x, and so on...)
	 */
	HashMap<Integer, Pose3d> tagHashMap = new HashMap<Integer, Pose3d>();

	/**
	 * This is the PhotonCamera instance, *** important: name it after your camera name in photonvision.
	 */
	PhotonCamera camera = new PhotonCamera("MicrosoftCamera");

	/**
	 * @return Boolean that returns weather or not PhotonVision has detected targets.
	 */
	public boolean hasTargets() {
		if (camera.getLatestResult().hasTargets())
			return true;
		return false;
	}

	/** @return The best target Photonvision found. */
	public PhotonTrackedTarget getBestTarget() {
		if (hasTargets())
			return camera.getLatestResult().getBestTarget();
		return null;
	}

	/** @return Best target's ID. */
	public int getTagID() {
		if (hasTargets())
			return getBestTarget().getFiducialId();
		return -1; // this is bad! ew tfu tfu tfu knock on wood, find a way to replace this in the
					// future, big ihsa...
	}

	/**
	 * @return Best target's Transform3D (dimentions between the camera and the tag, reletive position to the tag).
	 */
	public Transform3d getRelativeTransform3D() {
		if (hasTargets())
			return getBestTarget().getBestCameraToTarget();
		return null;
	}

	/** @return Best target's X. */
	public Double getRelativeX() {
		if (hasTargets())
			return getRelativeTransform3D().getX();
		return null;
	}

	/** @return Best target's Y. */
	public Double getRelativeY() {
		if (hasTargets())
			return getRelativeTransform3D().getY();
		return null;
	}

	/** @return Best target's Z. */
	public Double getRelativeZ() {
		if (hasTargets())
			return getRelativeTransform3D().getZ();
		return null;
	}

	/** @return Robot's Pose3D on the field. */
	public Pose3d getFieldPose3D() {

		PhotonTrackedTarget target = getBestTarget();
		int TagID = target.getFiducialId();
		Pose3d tagPose3d = tagHashMap.get(TagID);

		var transform = this.getRelativeTransform3D();
		if (transform != null) {
			Pose3d RobotPose3d = tagPose3d.transformBy(transform);
			return RobotPose3d;
		}
		return null;
	}

	/** @return Robot's X on the field. */
	public Double getFieldX() {
		if (hasTargets())
			return getFieldPose3D().getX();
		return null;
	}

	/** @return Robot's Y on the field. */
	public Double getFieldY() {
		if (hasTargets())
			return getFieldPose3D().getY();
		return null;
	}

	/** @return Robot's Z on the field. */
	public Double getFieldZ() {
		if (hasTargets())
			return getFieldPose3D().getZ();
		return null;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("PhotonVision");

		builder.addDoubleProperty("Field X", this::getFieldX, null);
		builder.addDoubleProperty("Field Y", this::getFieldY, null);
		builder.addDoubleProperty("Field Z", this::getFieldZ, null);

		builder.addDoubleProperty("Relative X", this::getRelativeX, null);
		builder.addDoubleProperty("Relative Y", this::getRelativeY, null);
		builder.addDoubleProperty("Relative Z", this::getRelativeZ, null);
	}
}