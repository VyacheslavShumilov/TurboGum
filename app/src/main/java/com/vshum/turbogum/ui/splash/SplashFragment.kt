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
import androidx.fragment.app.Fragment
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
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
        val appUpdateManager = AppUpdateManagerFactory.create(activity)

        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                if (!isAdded) return@addOnSuccessListener

                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    Log.d(TAG, "Update available — launching flexible update dialog")
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                        )
                        // navigateHome() will be called from updateLauncher result
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to start update flow", e)
                        scheduleNavigateHome()
                    }
                } else {
                    Log.d(TAG, "No update available or not allowed — proceeding normally")
                    scheduleNavigateHome()
                }
            }
            .addOnFailureListener { e ->
                // Play Store unreachable (offline, emulator, etc.) — proceed normally
                Log.w(TAG, "Update check failed: ${e.message}")
                if (isAdded) scheduleNavigateHome()
            }
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
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavVisible(false)
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