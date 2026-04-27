package com.vshum.turbogum.ui.liner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
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

    // Overlay views живут на уровне Activity — поверх всего экрана
    private val imageOverlay: View by lazy {
        requireActivity().findViewById(R.id.imageOverlay)
    }
    private val expandedImage: ImageView by lazy {
        requireActivity().findViewById(R.id.expandedImage)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinerBinding.inflate(inflater, container, false)

        with(binding) {

            // ── Скрываем кнопки если данных нет ───────────────────────
            if (liner.video == "-")          containerVideo.visibility   = View.GONE
            if (liner.vkArticle == "-")      containerVk.visibility      = View.GONE
            if (liner.wikiArticle == "-")    containerWiki.visibility    = View.GONE
            if (liner.websiteSociete == "-") containerSociete.visibility = View.GONE

            // ── Изображение ────────────────────────────────────────────
            if (liner.imageUrlLiner.isNotEmpty()) {
                Picasso.get()
                    .load(liner.imageUrlLiner)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.placeholder)
            }

            // ── Текстовые поля ─────────────────────────────────────────
            linerNumber.text = liner.numberLiner
            linerBrand.text  = liner.brand
            linerModel.text  = liner.model
            tagSeries.text   = liner.series

            // ── Кнопка назад ───────────────────────────────────────────
            toWrappersBtn.setOnClickListener {
                appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
            }

            // ── Внешние ссылки ─────────────────────────────────────────
            containerVideo.setOnClickListener   { openUrl(liner.video) }
            containerVk.setOnClickListener      { openUrl(liner.vkArticle) }
            containerWiki.setOnClickListener    { openUrl(liner.wikiArticle) }
            containerSociete.setOnClickListener { openUrl(liner.websiteSociete) }

            // ── Проверка избранного в БД ───────────────────────────────
            lifecycleScope.launch(Dispatchers.IO) {
                val linerFav = (context?.applicationContext as App)
                    .getDatabase().linersDao().getLinerFavorite(liner.uniqueNumber)
                withContext(Dispatchers.Main) {
                    val isFav = linerFav != null
                    containerFav.isClickable = !isFav
                    containerFav.alpha = if (isFav) 0.5f else 1f
                }
            }

            containerFav.setOnClickListener { addToFavourite() }

            // ── Клик на картинку — открыть на весь экран ──────────────
            imageView.setOnClickListener {
                if (!isImageExpanded) expandImage() else collapseImage()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Убираем оверлей при уходе с экрана
        if (isImageExpanded) {
            imageOverlay.visibility = View.GONE
            expandedImage.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE
            isImageExpanded = false
        }
    }

    // ── Добавить в избранное ───────────────────────────────────────────────

    private fun addToFavourite() {
        val linerFavourite = LinersFavourite(
            key           = 0,
            uniqueNumber  = liner.uniqueNumber,
            id            = liner.id,
            numberLiner   = liner.numberLiner,
            brand         = liner.brand,
            model         = liner.model,
            wikiArticle   = liner.wikiArticle,
            websiteSociete = liner.websiteSociete,
            video         = liner.video,
            vkArticle     = liner.vkArticle,
            imageUrlLiner = liner.imageUrlLiner,
            index         = liner.index,
            series        = liner.series,
            note          = liner.note
        )

        CoroutineScope(Dispatchers.IO).launch {
            (context?.applicationContext as App).getDatabase()
                .linersDao().insertLiner(linerFavourite)
        }

        binding.containerFav.animate()
            .alpha(0.5f)
            .setDuration(200)
            .start()
        binding.containerFav.isClickable = false
    }

    // ── Открыть внешнюю ссылку ────────────────────────────────────────────

    private fun openUrl(url: String) {
        if (url.isNotBlank() && url != "-") {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    // ── Анимация: открыть изображение поверх экрана ───────────────────────

    private fun expandImage() {
        binding.imageView.visibility = View.INVISIBLE

        // Затемнение фона
        imageOverlay.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Изображение появляется с масштабированием
        expandedImage.apply {
            setImageDrawable(binding.imageView.drawable)
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.75f
            scaleY = 0.75f
        }

        expandedImage.doOnPreDraw {
            expandedImage.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Клик на оверлей или картинку — закрыть
        imageOverlay.setOnClickListener { collapseImage() }
        expandedImage.setOnClickListener { collapseImage() }

        isImageExpanded = true
    }

    // ── Анимация: закрыть изображение ────────────────────────────────────

    private fun collapseImage() {
        imageOverlay.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        expandedImage.animate()
            .alpha(0f)
            .scaleX(0.75f)
            .scaleY(0.75f)
            .setDuration(250)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                imageOverlay.visibility = View.GONE
                expandedImage.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
            }
            .start()

        isImageExpanded = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}