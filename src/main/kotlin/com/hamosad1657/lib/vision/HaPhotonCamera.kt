package com.hamosad1657.lib.vision

import com.hamosad1657.lib.Alert
import com.hamosad1657.lib.Alert.AlertType
import org.photonvision.PhotonCamera
import org.photonvision.targeting.PhotonPipelineResult

class HaPhotonCamera(val cameraName: String) : PhotonCamera(cameraName) {

	/**
	 * This alert is updated active/inactive on every call to isConnected().
	 * You may also update it yourself.
	 */
	val disconnectedAlert = Alert("$cameraName disconnected", AlertType.ERROR)
	override fun isConnected(): Boolean {
		return super.isConnected().also { disconnectedAlert.set(!it) }
	}

	override fun getLatestResult(): PhotonPipelineResult? = if (isConnected) super.getLatestResult() else null

	/**
	 * This function is no-op if the camera is currently disconnected.
	 * - Images take up space in the disk of the coprocessor running PhotonVision.
	 * 	 Calling take snapshot frequently will fill up disk space and eventually cause the system to stop working.
	 * 	 Clear out images in /opt/photonvision/photonvision_config/imgSaves to prevent issues.
	 */
	override fun takeInputSnapshot() = doIfConnected { super.takeInputSnapshot() }
	/**
	 * This function is no-op if the camera is currently disconnected.
	 * - Images take up space in the disk of the coprocessor running PhotonVision.
	 * 	 Calling take snapshot frequently will fill up disk space and eventually cause the system to stop working.
	 * 	 Clear out images in /opt/photonvision/photonvision_config/imgSaves to prevent issues.
	 */
	override fun takeOutputSnapshot() = doIfConnected { super.takeOutputSnapshot() }

	fun doIfConnected(toDo: () -> Unit) {
		if (!isConnected) return
		toDo()
	}
}