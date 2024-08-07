package com.hamosad1657.lib.vision

import com.hamosad1657.lib.units.Length
import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import org.photonvision.EstimatedRobotPose
import org.photonvision.PhotonCamera
import org.photonvision.PhotonPoseEstimator
import org.photonvision.PhotonPoseEstimator.PoseStrategy
import org.photonvision.PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR
import org.photonvision.targeting.PhotonTrackedTarget
import kotlin.jvm.optionals.getOrNull

private val TAGS_LAYOUT = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()

abstract class AprilTagCamera(private val camera: HaPhotonCamera) {
	init {
		camera.driverMode = false
	}
	protected abstract val robotToCamera: Transform3d
	abstract val maxTagTrustingDistance: Length
	abstract val stdDevs: AprilTagsStdDevs
	abstract val isAutonomousSupplier: () -> Boolean

	// Try to retrieve the pose estimator again only if it is null.
	private var _poseEstimator: PhotonPoseEstimator? = null
	private val poseEstimator: PhotonPoseEstimator
		get() =
			_poseEstimator ?: PhotonPoseEstimator(
				TAGS_LAYOUT,
				MULTI_TAG_PNP_ON_COPROCESSOR,
				camera,
				robotToCamera,
			).apply {
				setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY)
			}.also {
				_poseEstimator = it
			}

	val isInRange: Boolean
		get() {
			val robotToTagDistance = bestTag?.bestCameraToTarget?.x ?: return false
			return robotToTagDistance < maxTagTrustingDistance.asMeters
		}

	val bestTag: PhotonTrackedTarget? get() = camera.latestResult?.bestTarget
	fun getTag(tagID: Int): PhotonTrackedTarget? = camera.latestResult?.targets?.find { it.fiducialId == tagID }
	fun isTagDetected(tagID: Int): Boolean = getTag(tagID) != null
	fun isAnyTagDetected(vararg tagIDs: Int): Boolean = tagIDs.any(::isTagDetected)

	/**
	 * Gets the estimated robot position from the PhotonVision camera.
	 * Updates every time the getter is called.
	 *
	 * Returns null if:
	 * - There is no camera
	 * - No AprilTags were detected
	 * - The RoboRIO hasn't received new data from PhotonVision since the last access.
	 */
	val estimatedPose: EstimatedRobotPose?
		get() = if (camera.isConnected) poseEstimator.update()?.getOrNull() else null

	val poseEstimationStdDevs
		get() = if (camera.latestResult?.targets?.size == 1) {
			stdDevs.oneTag
		} else if (isAutonomousSupplier()) {
			stdDevs.twoTagsAuto
		} else {
			stdDevs.twoTagsTeleop
		}

	data class AprilTagsStdDevs(
		val oneTag: RobotPoseStdDevs,
		val twoTagsAuto: RobotPoseStdDevs,
		val twoTagsTeleop: RobotPoseStdDevs,
	)
}

class RobotPoseStdDevs(
	translationX: Double,
	translationY: Double,
	rotation: Double,
) : Matrix<N3, N1>(Nat.N3(), Nat.N1()) {
	init {
		this[0, 0] = translationX
		this[1, 0] = translationY
		this[2, 0] = rotation
	}
}
