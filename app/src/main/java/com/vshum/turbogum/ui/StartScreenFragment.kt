package com.vshum.turbogum.ui
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentStartScreenBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen

class StartScreenFragment : Fragment() {

    private lateinit var binding: FragmentStartScreenBinding
    private lateinit var appNavigator: AppNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        with(binding) {
//            logoImage.setOnClickListener {
//                playIntroVideo {
//                    // После окончания видео → переход
//                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
//                }
//            }
//
//            developersBtn.setOnClickListener {
//                appNavigator.navigateTo(Screen.DEVELOPERS_SCREEN)
//            }
//        }
    }

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        )
    }

    // Видео перед переходом на другой экран
    private fun playIntroVideo(onFinish: () -> Unit) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_video)

        val videoView = dialog.findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://${requireContext().packageName}/${R.raw.intro}"
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener { mp ->
            // Уменьшаем громкость до 30%
            mp.setVolume(0.3f, 0.3f)

            mp.isLooping = false

            // Масштабируем видео на весь экран с сохранением пропорций
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            }

            videoView.start()
        }

        videoView.setOnCompletionListener {
            dialog.dismiss()
            onFinish()
        }

        dialog.show()
    }
}
