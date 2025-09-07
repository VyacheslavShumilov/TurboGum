package com.vshum.turbogum.ui.liner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
//import com.squareup.picasso.Picasso
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentLinerBinding
import com.vshum.turbogum.model.Liner
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LinerFragment(var liner: Liner) : Fragment() {

    private lateinit var binding: FragmentLinerBinding
    private lateinit var appNavigator: AppNavigator
    private var isImageExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLinerBinding.inflate(inflater, container, false)

        with(binding) {

            // Скрытие контейнеров, если данных нет
            if (liner.video == "-") containerVideo.visibility = View.GONE
            if (liner.vkArticle == "-") containerVk.visibility = View.GONE
            if (liner.wikiArticle == "-") containerWiki.visibility = View.GONE
            if (liner.websiteSociete == "-") containerSociete.visibility = View.GONE

            // Загрузка изображения
            if (liner.imageUrlLiner.isEmpty()) {
                imageView.setImageResource(R.drawable.placeholder)
            } else {
                Glide.with(this@LinerFragment)
                    .load(liner.imageUrlLiner)
                    .into(imageView)
            }

            linerIndex.text = liner.index
            linerNumber.text = liner.numberLiner
            linerBrand.text = liner.brand
            linerModel.text = liner.model

            // Навигация toolbar
            binding.toWrappersBtn.setOnClickListener {
                appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
            }

            containerVideo.setOnClickListener { openUrl(liner.video) }
            containerVk.setOnClickListener { openUrl(liner.vkArticle) }
            containerWiki.setOnClickListener { openUrl(liner.wikiArticle) }
            containerSociete.setOnClickListener { openUrl(liner.websiteSociete) }

            // Избранное
            btnAddFavourite.setImageDrawable(resources.getDrawable(R.drawable.btn_fav_outlined))
            containerFav.setOnClickListener { addToFavourite() }

            // Toolbar кнопка избранного
            val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
            binding.toFavouriteBtn.setOnClickListener {
                binding.toFavouriteBtn.startAnimation(fadeOut)
                Handler(Looper.myLooper()!!).postDelayed({
                    appNavigator.navigateTo(Screen.FAVOURITE)
                }, 400)
            }

            // Проверка избранного в БД
            lifecycleScope.launch(Dispatchers.IO) {
                val linerFav = (context?.applicationContext as App)
                    .getDatabase().linersDao().getLinerFavorite(liner.uniqueNumber)
                withContext(Dispatchers.Main) {
                    containerFav.isClickable = linerFav == null
                    btnAddFavourite.setImageDrawable(
                        resources.getDrawable(
                            if (containerFav.isClickable) R.drawable.btn_fav_outlined
                            else R.drawable.btn_fav
                        )
                    )
                }
            }

            // --- Увеличение изображения ---
            imageView.setOnClickListener {
                if (!isImageExpanded) expandImage() else collapseImage()
            }

            expandedImage.setOnClickListener {
                collapseImage()
            }
        }

        return binding.root
    }

    private fun openUrl(url: String) {
        if (url != "-") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun addToFavourite() {
        val linerFavourite = LinersFavourite(
            0,
            liner.uniqueNumber,
            liner.id,
            liner.numberLiner,
            liner.brand,
            liner.model,
            liner.wikiArticle,
            liner.websiteSociete,
            liner.video,
            liner.vkArticle,
            liner.imageUrlLiner,
            liner.index,
            liner.series,
            liner.note
        )

        CoroutineScope(Dispatchers.IO).launch {
            (context?.applicationContext as App).getDatabase().linersDao()
                .insertLiner(linerFavourite)
        }

        val scaleAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.btn_scale_anim)
        binding.btnAddFavourite.startAnimation(scaleAnim)
        binding.btnAddFavourite.setImageDrawable(resources.getDrawable(R.drawable.btn_fav))
        binding.containerFav.isClickable = false
    }

    private fun expandImage() {
        binding.expandedImage.post {
            with(binding) {
                val originalLocation = IntArray(2)
                imageView.getLocationOnScreen(originalLocation)
                val originalX = originalLocation[0].toFloat()
                val originalY = originalLocation[1].toFloat()
                val originalWidth = imageView.width.toFloat()
                val originalHeight = imageView.height.toFloat()

                expandedImage.setImageDrawable(imageView.drawable)
                imageOverlay.visibility = View.VISIBLE
                expandedImage.visibility = View.VISIBLE

                // Начальные координаты
                expandedImage.pivotX = 0f
                expandedImage.pivotY = 0f
                expandedImage.translationX = originalX
                expandedImage.translationY = originalY

                // Безопасный масштаб (чтобы не делить на ноль)
                val safeWidth = expandedImage.width.takeIf { it > 0 } ?: 1
                val safeHeight = expandedImage.height.takeIf { it > 0 } ?: 1

                val scaleX = originalWidth / safeWidth.toFloat()
                val scaleY = originalHeight / safeHeight.toFloat()
                val startScale = minOf(scaleX, scaleY)

                expandedImage.scaleX = startScale
                expandedImage.scaleY = startScale
                expandedImage.alpha = 0f

                imageView.visibility = View.INVISIBLE

                // Анимация увеличения
                expandedImage.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()

                isImageExpanded = true
            }
        }
    }

    private fun collapseImage() {
        binding.expandedImage.post {
            with(binding) {
                val originalLocation = IntArray(2)
                imageView.getLocationOnScreen(originalLocation)
                val originalX = originalLocation[0].toFloat()
                val originalY = originalLocation[1].toFloat()
                val originalWidth = imageView.width.toFloat()
                val originalHeight = imageView.height.toFloat()

                val safeWidth = expandedImage.width.takeIf { it > 0 } ?: 1
                val safeHeight = expandedImage.height.takeIf { it > 0 } ?: 1

                val scaleX = originalWidth / safeWidth.toFloat()
                val scaleY = originalHeight / safeHeight.toFloat()
                val endScale = minOf(scaleX, scaleY)

                expandedImage.animate()
                    .translationX(originalX)
                    .translationY(originalY)
                    .scaleX(endScale)
                    .scaleY(endScale)
                    .alpha(0f)
                    .setDuration(400)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction {
                        imageOverlay.visibility = View.GONE
                        expandedImage.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }
                    .start()

                isImageExpanded = false
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}











