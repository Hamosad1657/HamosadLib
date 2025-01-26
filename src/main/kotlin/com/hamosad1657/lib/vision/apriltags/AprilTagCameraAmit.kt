package frc.robot.vision

import com.ctre.phoenix6.hardware.Pigeon2
import com.hamosad1657.lib.units.Length
import com.hamosad1657.lib.units.degrees
import com.hamosad1657.lib.units.meters
import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields.k2025Reefscape
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation2d
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
import kotlin.math.absoluteValue


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

		//Find StdDevs
		var lastEstimatedPose: Pose3d? = null
		var lastPigeonAngle: Rotation2d = 0.0.degrees
		val xErrorsList: MutableList<Double> = emptyList<Double>().toMutableList()
		val yErrorsList: MutableList<Double> = emptyList<Double>().toMutableList()
		val rotationErrorsList: MutableList<Double> = emptyList<Double>().toMutableList()
		val timer = edu.wpi.first.wpilibj.Timer()
		val xStdDevs = calculateGeneralStdDevs(xErrorsList.toDoubleArray())
		val yStdDevs = calculateGeneralStdDevs(yErrorsList.toDoubleArray())
		val rotationStdDevs = calculateGeneralStdDevs(rotationErrorsList.toDoubleArray())
		val cameraPoseStdDevs = RobotPoseStdDevs(xStdDevs, yStdDevs, rotationStdDevs)

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

		fun calculateGeneralStdDevs(numArray: DoubleArray): Double {
			var sum = 0.0
			var standardDeviation = 0.0

			for (num in numArray) {
				sum += num
			}

			val mean = sum / 10

			for (num in numArray) {
				standardDeviation += Math.pow(num - mean, 2.0)
			}

			return Math.sqrt(standardDeviation / 10)
		}

		fun calculatePositionStdDevs(pigeon: Pigeon2) {
			lastEstimatedPose?.let {
				timer.start()
				if (timer.hasElapsed(0.2)) {
					rotationErrorsList.add(((estimatedGlobalPose!!.estimatedPose.rotation.angle - lastEstimatedPose!!.rotation.angle).absoluteValue / 0.02) - ((pigeon.getYaw().valueAsDouble - lastPigeonAngle.degrees).absoluteValue / 0.02))
					xErrorsList.add(((estimatedGlobalPose!!.estimatedPose.x - lastEstimatedPose!!.x).absoluteValue / 0.02) - pigeon.accelerationX.valueAsDouble)
					yErrorsList.add(((estimatedGlobalPose!!.estimatedPose.y - lastEstimatedPose!!.y).absoluteValue / 0.02) - pigeon.accelerationY.valueAsDouble)

					if (xErrorsList.size > 10) {
						xErrorsList.removeAt(10)
					}
					if (yErrorsList.size > 10) {
						yErrorsList.removeAt(10)
					}
					if (rotationErrorsList.size > 10) {
						rotationErrorsList.removeAt(10)
					}

					timer.reset()
					timer.stop()
				}
			}
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

