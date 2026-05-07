package com.vshum.turbogum.utils

import android.app.Activity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class UpdateManager(private val activity: Activity) {

    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(activity)

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showRestartSnackbar()
        }
    }

    fun checkForUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

            val isUpdateAllowed =
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

            if (isUpdateAvailable && isUpdateAllowed) {
                startFlexibleUpdate(appUpdateInfo)
            }
        }.addOnFailureListener {
            // Обновление не доступно или произошла ошибка
        }
    }

    private fun startFlexibleUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlow(
            appUpdateInfo,
            activity,
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
        )

        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    private fun showRestartSnackbar() {
        Snackbar.make(
            activity.window.decorView.rootView,
            "Обновление загружено",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Перезагрузить") {
            appUpdateManager.completeUpdate()
        }.show()
    }

    fun unregisterListener() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}