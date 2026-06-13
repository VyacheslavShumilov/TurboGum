package com.vshum.turbogum.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.data.AuthRepository
import com.vshum.turbogum.data.UserRepository
import com.vshum.turbogum.databinding.FragmentProfileBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Profile screen.
 * Header, user identity card, stats grid (collected / favourites / completion),
 * per-series progress list, menu cards (settings / notifications / developers).
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository

    /** All series with their total counts, used for progress aggregation. */
    private data class SeriesProgressEntry(
        val seriesKey: String,
        val label: String,
        val total: Int
    )

    private val seriesEntries = listOf(
        SeriesProgressEntry("Серия 1", "Серия 1", 50),
        SeriesProgressEntry("Серия 2", "Серия 2", 70),
        SeriesProgressEntry("Серия 3", "Серия 3", 70),
        SeriesProgressEntry("Серия 4", "Серия 4", 70),
        SeriesProgressEntry("Серия 5", "Серия 5", 70),
        SeriesProgressEntry("Super 1", "Super 1", 70),
        SeriesProgressEntry("Super 2", "Super 2", 70),
        SeriesProgressEntry("Super 3", "Super 3", 70)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenuClicks()
        loadStats()
        loadNickname()
    }

    private fun setupMenuClicks() {
        binding.menuSettings.setOnClickListener {
            // Settings screen not yet implemented — placeholder
        }
        binding.menuNotifications.setOnClickListener {
            // Notifications screen not yet implemented — placeholder
        }
        binding.menuDevelopers.setOnClickListener {
            appNavigator.navigateTo(Screen.DEVELOPERS_SCREEN)
        }
        binding.btnEditNickname.setOnClickListener {
            showEditNicknameDialog()
        }
        binding.menuLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadNickname() {
        val uid = authRepository.currentUser?.uid ?: return
        userRepository.getUser(
            uid,
            onResult = { profile ->
                val nickname = profile?.nickname?.takeIf { it.isNotBlank() }
                    ?: authRepository.currentUser?.displayName?.takeIf { it.isNotBlank() }
                    ?: getString(R.string.profile_nickname_default)
                binding.userName.text = nickname
            },
            onError = { }
        )
    }

    private fun showEditNicknameDialog() {
        val uid = authRepository.currentUser?.uid ?: return
        val input = EditText(requireContext()).apply {
            setText(binding.userName.text)
            hint = getString(R.string.profile_nickname_hint)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.profile_nickname_edit_title)
            .setView(input)
            .setPositiveButton(R.string.detail_note_save) { _, _ ->
                val nickname = input.text.toString().trim()
                if (nickname.isNotEmpty()) {
                    binding.userName.text = nickname
                    userRepository.updateNickname(uid, nickname, onComplete = {}, onError = {})
                }
            }
            .setNegativeButton(R.string.detail_note_cancel, null)
            .show()
    }

    private fun logout() {
        authRepository.signOut()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(requireActivity(), options).signOut()
        appNavigator.navigateTo(Screen.LOGIN_SCREEN)
    }

    private fun loadStats() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = (requireContext().applicationContext as App)
                .getDatabase().linersDao()
            val all = try { dao.getAllFavouriteLiners() } catch (e: Exception) { emptyList() }
            val total = seriesEntries.sumOf { it.total }
            val collected = all.size
            val percent = if (total > 0) (collected * 100 / total) else 0

            // Group counts per series for the progress list
            val perSeries = all.groupingBy { it.series }.eachCount()

            withContext(Dispatchers.Main) {
                binding.statCollected.text = collected.toString()
                binding.statFavourites.text = collected.toString()
                binding.statCompletion.text = "$percent%"
                buildSeriesProgress(perSeries)
            }
        }
    }

    private fun buildSeriesProgress(counts: Map<String, Int>) {
        val container = binding.progressList
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        seriesEntries.forEach { entry ->
            val row = inflater.inflate(R.layout.item_series_progress, container, false) as LinearLayout
            val owned = counts[entry.seriesKey] ?: 0
            val percent = if (entry.total > 0) (owned * 100 / entry.total) else 0

            row.findViewById<TextView>(R.id.seriesProgLabel)?.text = entry.label
            row.findViewById<TextView>(R.id.seriesProgCount)?.text = "$owned/${entry.total}"
            row.findViewById<ProgressBar>(R.id.seriesProgBar)?.progress = percent

            container.addView(row)
        }
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val servicesLocator = (context.applicationContext as App).servicesLocator
        appNavigator = servicesLocator.providerNavigator(requireActivity())
        authRepository = servicesLocator.providerAuthRepository()
        userRepository = servicesLocator.providerUserRepository()
    }
}