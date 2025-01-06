package com.hamosad1657.lib.vision.apriltags

import com.hamosad1657.lib.units.Length
import com.hamosad1657.lib.vision.HaPhotonCamera
import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import org.photonvision.EstimatedRobotPose
import org.photonvision.PhotonPoseEstimator
import org.photonvision.PhotonPoseEstimator.PoseStrategy
import org.photonvision.PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR
import org.photonvision.targeting.PhotonPipelineResult
import org.photonvision.targeting.PhotonTrackedTarget
import kotlin.jvm.optionals.getOrNull

// TODO: Change AprilTag field layout to 2025 Reefscape when it releases
private val TAGS_LAYOUT = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField)

/**
 * From all the detected tags in this pipeline result, finds and returns one with the specified ID.
 * If a tag with the specified ID is not present, returns null.
 */
fun PhotonPipelineResult.getTag(tagID: Int): PhotonTrackedTarget? = targets?.find { it?.fiducialId == tagID }

/**
 * From the detected tags in this pipeline result, returns whether at least one with the specified
 * ID is present.
 */
fun PhotonPipelineResult.isTagDetected(tagID: Int): Boolean = getTag(tagID) != null

/**
 * From all the detected tags in this pipeline result, returns whether at least one with one of the
 * specified IDs is present.
 */
fun PhotonPipelineResult.isAnyTagDetected(vararg tagIDs: Int): Boolean = tagIDs.any(::isTagDetected)

val PhotonPipelineResult.amountOfTagsDetected: Int get() = if (hasTargets())targets?.size ?: 0 else 0


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
				robotToCamera,
			).apply {
				setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY)
			}.also {
				_poseEstimator = it
			}

	/**
	 * In HaPhotonCamera, latestResult will be null if the camera is disconnected.
	 * This is done to prevent the use of data older than (as of 2024) half a second.
	 *
	 * Nevertheless, in timing-sensitive calculations, compensate for delay by using
	 * the timestamp included in [PhotonPipelineResult] instances.
	 */
	val latestResult: PhotonPipelineResult? get() = camera.allUnreadResults.first()

	fun getCameraToTagDistance(pipelineResult: PhotonPipelineResult?): Length? = (pipelineResult?.bestTarget?.bestCameraToTarget?.x)?.let {
			Length.fromMeters(it)
		}

	fun isInRange(pipelineResult: PhotonPipelineResult?): Boolean {
		val distance = getCameraToTagDistance(pipelineResult) ?: return false
		return distance < maxTagTrustingDistance
	}

	/**
	 * Updates the PhotonVision pose estimator with [pipelineResult] and returns
	 * the updated [EstimatedRobotPose], which includes the position in the field,
	 * the timestamp, the tags used and the pose estimation strategy used.
	 *
	 * Returns null if:
	 * - [pipelineResult] is null
	 * - There are no tags in [pipelineResult]
	 * - The timestamp of [pipelineResult] is not newer than the previous update.
	 */
	fun updatePhotonPoseEstimator(pipelineResult: PhotonPipelineResult?): EstimatedRobotPose? {
		if (pipelineResult == null) return null
		return poseEstimator.update(pipelineResult)?.getOrNull()
	}

	fun getPoseEstimationStdDevs(pipelineResult: PhotonPipelineResult?) =
		if (pipelineResult?.amountOfTagsDetected == 1) {
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
