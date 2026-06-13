package com.vshum.turbogum.ui.leaderboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.vshum.turbogum.App
import com.vshum.turbogum.databinding.FragmentLeaderboardBinding
import com.vshum.turbogum.model.SeriesStats
import com.vshum.turbogum.model.UserProfile
import com.vshum.turbogum.ui.leaderboard.adapter.AdapterLeaderboard
import com.vshum.turbogum.ui.leaderboard.impl.LeaderboardContract
import com.vshum.turbogum.ui.leaderboard.impl.LeaderboardPresenterImpl
import kotlin.math.roundToInt

/** Leaderboard screen: chip filters per series + realtime ranked list, current user pinned at bottom. */
class LeaderboardFragment : Fragment(), LeaderboardContract.View {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: LeaderboardContract.Presenter
    private lateinit var adapter: AdapterLeaderboard
    private lateinit var authRepository: com.vshum.turbogum.data.AuthRepository
    private lateinit var userRepository: com.vshum.turbogum.data.UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = LeaderboardPresenterImpl(this, authRepository, userRepository)

        adapter = AdapterLeaderboard(emptyList(), LeaderboardPresenterImpl.DEFAULT_FIELD, presenter.currentUid())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LeaderboardFragment.adapter
        }

        setupChips()
        presenter.selectFilter(LeaderboardPresenterImpl.DEFAULT_FIELD)
    }

    private fun setupChips() {
        val context = requireContext()

        val allChip = Chip(context).apply {
            id = View.generateViewId()
            text = "Все"
            tag = SeriesStats.TOTAL_PERCENT_KEY
            isCheckable = true
            isChecked = true
            setChipBackgroundColorResource(com.vshum.turbogum.R.color.bg_surface_1)
            setChipStrokeColorResource(com.vshum.turbogum.R.color.border_1)
            chipStrokeWidth = 0.5f
        }
        binding.filterChipGroup.addView(allChip)

        SeriesStats.all.forEach { stat ->
            val chip = Chip(context).apply {
                id = View.generateViewId()
                text = stat.label
                tag = stat.key
                isCheckable = true
                setChipBackgroundColorResource(com.vshum.turbogum.R.color.bg_surface_1)
                setChipStrokeColorResource(com.vshum.turbogum.R.color.border_1)
                chipStrokeWidth = 0.5f
            }
            binding.filterChipGroup.addView(chip)
        }

        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val field = group.findViewById<Chip>(checkedId).tag as String
            presenter.selectFilter(field)
        }
    }

    override fun onUsers(users: List<UserProfile>, field: String) {
        if (_binding == null) return

        adapter.update(users, field)
        binding.emptyState.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (users.isEmpty()) View.GONE else View.VISIBLE

        val currentUid = presenter.currentUid()
        val index = users.indexOfFirst { it.uid == currentUid }
        if (index >= 0) {
            val user = users[index]
            binding.currentUserContainer.visibility = View.VISIBLE
            binding.currentUserRow.rankText.text = "#${index + 1}"
            binding.currentUserRow.nicknameText.text = user.nickname.ifBlank { "Коллекционер" }
            binding.currentUserRow.percentText.text = "${user.percentFor(field).roundToInt()}%"
            binding.currentUserRow.root.setBackgroundResource(com.vshum.turbogum.R.drawable.bg_leaderboard_highlight)
        } else {
            binding.currentUserContainer.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val servicesLocator = (context.applicationContext as App).servicesLocator
        authRepository = servicesLocator.providerAuthRepository()
        userRepository = servicesLocator.providerUserRepository()
    }
}
