package com.hamosad1657.lib.vision

import com.hamosad1657.lib.Alert
import com.hamosad1657.lib.Alert.AlertType
import com.hamosad1657.lib.robotPrint
import com.hamosad1657.lib.robotPrintError
import org.photonvision.PhotonCamera
import org.photonvision.targeting.PhotonPipelineResult

class HaPhotonCamera(val cameraName: String) : PhotonCamera(cameraName) {

	/**
	 * This alert is updated active/inactive on every call to isConnected().
	 * You may also update it yourself.
	 */
	val disconnectedAlert = Alert("$cameraName disconnected", AlertType.ERROR)

	/**
	 * Returns whether the camera is actively sending new data.
	 *
	 * Rising edge (false to true) transitions happen immediately, while falling
	 * edge (true to false) transitions are debounced by half a second, as of 2024.
	 */
	override fun isConnected(): Boolean {
		return super.isConnected().also { disconnectedAlert.set(!it) }
	}

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

	init {
		robotPrint("Initialized HaPhotonCamera instance: $cameraName")
		if (isConnected) {
			robotPrint("$cameraName connected!")
		} else {
			robotPrintError("$cameraName disconnected!")
		}
		super.getAllUnreadResults() // This triggers a call to verifyVersion() in PhotonCamera, regardless of whether the camera is connected.
	}
}