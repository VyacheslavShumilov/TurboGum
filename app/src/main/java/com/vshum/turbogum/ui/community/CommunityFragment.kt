package com.vshum.turbogum.ui.community

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vshum.turbogum.App
import com.vshum.turbogum.databinding.FragmentCommunityBinding
import com.vshum.turbogum.navigator.AppNavigator

/**
 * Community screen.
 *
 * Currently provides one-tap access to the VK "Турбо Товарищи" community.
 * Future: integrate a Retrofit feed endpoint or a WebView showing community posts.
 */
class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var appNavigator: AppNavigator

    // VK community URL — same value stored on each Liner model
    private val vkCommunityUrl = "https://vk.com/turbo_societe"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Primary CTA: open VK community in browser / VK app
        binding.btnOpenVk.setOnClickListener {
            openUrl(vkCommunityUrl)
        }

        // Secondary: open societeturbo.com
        binding.btnOpenSociete.setOnClickListener {
            openUrl("https://societeturbo.com")
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}