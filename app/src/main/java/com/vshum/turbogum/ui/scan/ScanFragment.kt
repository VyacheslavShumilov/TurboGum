package com.vshum.turbogum.ui.scan

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentScanBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private lateinit var appNavigator: AppNavigator
    private var scanLineAnimator: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnManualEntry.setOnClickListener {
            // TODO: show manual card number entry dialog
        }

        binding.btnFlash.setOnClickListener {
            // TODO: toggle torch
        }

        startScanLineAnimation()
    }

    private fun startScanLineAnimation() {
        binding.cameraContainer.post {
            val containerHeight = binding.cameraContainer.height.toFloat()
            scanLineAnimator = ObjectAnimator.ofFloat(
                binding.scanLine, "translationY",
                0f, containerHeight - binding.scanLine.height
            ).apply {
                duration = 1800
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scanLineAnimator?.cancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}