package com.vshum.turbogum.ui.splash

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.vshum.turbogum.App
import com.vshum.turbogum.MainActivity
import com.vshum.turbogum.R
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

/**
 * Splash screen — shown briefly on launch.
 *
 * Update flow:
 *   1. Ask Play Store if an update is available (async, ~300ms).
 *   2a. Update available → launch FLEXIBLE in-app update dialog.
 *      User can accept or dismiss — either way we navigate to Home after.
 *   2b. No update / error → navigate to Home after 1.5 s as before.
 *   2c. Download completes (DOWNLOADED) → show AlertDialog to restart.
 *   2d. onResume with already-DOWNLOADED update → show the same dialog immediately.
 *
 * We use FLEXIBLE (not IMMEDIATE) so the user is never forced to wait —
 * the download happens in the background while they use the app.
 */
class SplashFragment : Fragment() {

    private lateinit var appNavigator: AppNavigator
    private val handler = Handler(Looper.getMainLooper())

    /** Launcher for the flexible update confirmation dialog. */
    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>

    /** Guard: navigate to Home only once. */
    private var navigatedToHome = false

    private var appUpdateManager: AppUpdateManager? = null

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdateReadyDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the update result launcher BEFORE the fragment is visible
        updateLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            // User accepted or dismissed the update dialog — go to Home either way
            when (result.resultCode) {
                Activity.RESULT_OK       -> Log.d(TAG, "Update accepted by user")
                Activity.RESULT_CANCELED -> Log.d(TAG, "Update dismissed by user")
                else                     -> Log.d(TAG, "Update flow result: ${result.resultCode}")
            }
            navigateHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForUpdate()
    }

    // ── Update check ──────────────────────────────────────────────────

    private fun checkForUpdate() {
        val activity = activity ?: run { scheduleNavigateHome(); return }

        appUpdateManager = AppUpdateManagerFactory.create(activity).also { manager ->
            manager.registerListener(installStateUpdatedListener)
        }

        appUpdateManager!!.appUpdateInfo
            .addOnSuccessListener { info ->
                if (!isAdded) return@addOnSuccessListener

                when {
                    info.installStatus() == InstallStatus.DOWNLOADED -> {
                        // Update was downloaded in a previous session and user postponed restart
                        Log.d(TAG, "Update already downloaded — showing restart dialog")
                        showUpdateReadyDialog()
                        scheduleNavigateHome()
                    }
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                        Log.d(TAG, "Update available — launching flexible update dialog")
                        try {
                            appUpdateManager!!.startUpdateFlowForResult(
                                info,
                                updateLauncher,
                                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                            )
                            // Download runs in background; installStateUpdatedListener fires on DOWNLOADED
                            // navigateHome() will be called from updateLauncher result
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to start update flow", e)
                            scheduleNavigateHome()
                        }
                    }
                    else -> {
                        Log.d(TAG, "No update available or not allowed — proceeding normally")
                        scheduleNavigateHome()
                    }
                }
            }
            .addOnFailureListener { e ->
                // Play Store unreachable (offline, emulator, etc.) — proceed normally
                Log.w(TAG, "Update check failed: ${e.message}")
                if (isAdded) scheduleNavigateHome()
            }
    }

    private fun showUpdateReadyDialog() {
        if (!isAdded) return
        // Unregister first so we don't show the dialog twice if the listener fires again
        appUpdateManager?.unregisterListener(installStateUpdatedListener)
        AlertDialog.Builder(requireContext())
            .setTitle("Обновление готово")
            .setMessage("Обновление загружено. Перезапустить приложение?")
            .setPositiveButton("Перезапустить") { _, _ ->
                appUpdateManager?.completeUpdate()
            }
            .setNegativeButton("Позже") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // ── Navigation ────────────────────────────────────────────────────

    /**
     * Navigate after the normal splash delay (1.5 s).
     * Used when no update is available or update check fails.
     */
    private fun scheduleNavigateHome() {
        handler.postDelayed({
            if (isAdded) navigateHome()
        }, 1500L)
    }

    private fun navigateHome() {
        if (navigatedToHome) return
        navigatedToHome = true
        appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        appUpdateManager?.unregisterListener(installStateUpdatedListener)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisible(false)
        // If user previously postponed a downloaded update, show dialog immediately on return
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info ->
            if (isAdded && info.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateReadyDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setBottomNavVisible(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }

    companion object {
        private const val TAG = "SplashUpdate"
    }
}
