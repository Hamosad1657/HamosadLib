package frc.robot.vision

import com.hamosad1657.lib.units.Length
import com.hamosad1657.lib.units.meters
import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields.k2025Reefscape
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.util.Units
import frc.robot.Robot
import org.photonvision.EstimatedRobotPose
import org.photonvision.PhotonCamera
import org.photonvision.PhotonPoseEstimator
import org.photonvision.PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR
import org.photonvision.PhotonUtils
import org.photonvision.targeting.PhotonPipelineResult
import org.photonvision.targeting.PhotonTrackedTarget
import kotlin.jvm.optionals.getOrNull


object AprilTagCameraAmit {
	class AprilTagCamera(cameraName: String, robotToCameraTranslation: Translation3d, robotToCameraRotation: Rotation3d) {
		val camera: PhotonCamera = PhotonCamera(cameraName)
		private val isConnected get() = camera.isConnected
		var result: PhotonPipelineResult? = null
		private val allTargets get() = result?.targets
		val hasTargets get() = result?.hasTargets()
		val bestTarget: PhotonTrackedTarget? get() = result?.bestTarget
		private val targetID get() = bestTarget?.fiducialId
		private val isAtAutonomous get() = Robot.isAutonomous

		private val MAX_RANGE = 5.0
		private val MAX_TAG_TRUSTING_DISTANCE: Length = 5.meters
		private val MAX_AMBIGUITY = 0.2

		private var idAndHeightMap: HashMap<Int, Double> = HashMap<Int, Double>().apply {
			put(1, 1.35255)
			put(2, 1.35255)
			put(3, 1.165225)
			put(4, 1.7526)
			put(5, 1.7526)
			put(6, 0.174625)
			put(7, 0.174625)
			put(8, 0.174625)
			put(9, 0.174625)
			put(10, 0.174625)
			put(11, 0.174625)
			put(12, 1.35255)
			put(13, 1.35255)
			put(14, 1.7526)
			put(15, 1.7526)
			put(16, 1.165225)
			put(17, 0.174625)
			put(18, 0.174625)
			put(19, 0.174625)
			put(20, 0.174625)
			put(21, 0.174625)
			put(22, 0.174625)
		}

		private val aprilTagFieldLayout: AprilTagFieldLayout = AprilTagFieldLayout.loadField(k2025Reefscape)

		private val robotToCamera = Transform3d(
			robotToCameraTranslation,
			robotToCameraRotation
		)

		private var photonPoseEstimator: PhotonPoseEstimator =
			PhotonPoseEstimator(aprilTagFieldLayout, MULTI_TAG_PNP_ON_COPROCESSOR, robotToCamera)

		val isInVisionRange: Boolean
			get() {
				if (hasTargets != null) {
					if (!hasTargets!!) return false
					for (i in camera.allUnreadResults.get(0).targets) {
						(idAndHeightMap[bestTarget?.fiducialId])?.let {
							if (calculateRange(it) < MAX_RANGE) {
								return true
							}
						}
					}
				}
				return false
			}

		val isInRange: Boolean
			get() {
				val robotToTagDistance = bestTarget?.bestCameraToTarget?.x ?: return false
				return robotToTagDistance < MAX_TAG_TRUSTING_DISTANCE.asMeters
			}

		private fun isTagDetected(tagId: Int): Boolean {
			if (hasTargets != null) {
				if (hasTargets as Boolean) {
					for (i in allTargets!!) {
						if (bestTarget?.fiducialId == tagId) {
							return true
						}
					}
				}
			}
			return false
		}


		private fun calculateRange(targetHeightMeters: Double): Double {
			return PhotonUtils.calculateDistanceToTargetMeters(
				robotToCamera.z,
				targetHeightMeters,
				robotToCamera.rotation.y,
				bestTarget?.let { Units.degreesToRadians(it.pitch) } ?: 0.0,
			)
		}

		val estimatedGlobalPose: EstimatedRobotPose?
			get() {
				if (!isConnected) return null
				result ?: return null
				if (hasTargets == null || bestTarget == null) return null
				if (bestTarget!!.poseAmbiguity > MAX_AMBIGUITY) return null
				return photonPoseEstimator.update(result).getOrNull()
			}
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
}

