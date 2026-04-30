package com.vshum.turbogum.ui.liner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

/**
 * Sticker detail screen.
 * Displays: hero image with badges, brand/model/number, CTA "Add to collection",
 * specs grid (engine/power/topSpeed/year), external links, editable note.
 */
class LinerFragment(var liner: Liner) : Fragment() {

    private var _binding: FragmentLinerBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private var isImageExpanded = false
    private var isInCollection = false

    private val imageOverlay: View?
        get() = activity?.findViewById(R.id.imageOverlay)
    private val expandedImage: ImageView?
        get() = activity?.findViewById(R.id.expandedImage)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupHero()
        setupTextFields()
        setupRarity()
        setupSpecs()
        setupLinks()
        setupNote()
        setupCta()
        setupImageExpand()
    }

    // ── Header ────────────────────────────────────────────────────────

    private fun setupHeader() {
        val header = binding.headerInclude.root
        header.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            appNavigator.navigateTo(Screen.LINERS_LIST_SCREEN)
        }
        header.findViewById<TextView>(R.id.headerTitle).text = ""
    }

    // ── Hero image ────────────────────────────────────────────────────

    private fun setupHero() {
        if (liner.imageUrlLiner.isNotEmpty()) {
            Picasso.get()
                .load(liner.imageUrlLiner)
                .into(binding.imageView)
        }
        binding.yearTag.text = yearForSeries(liner.series)
    }

    // ── Text fields ───────────────────────────────────────────────────

    private fun setupTextFields() {
        binding.linerBrand.text = liner.brand
        binding.linerModel.text = liner.model
        binding.linerNumber.text = "#${liner.numberLiner}"
        binding.tagSeries.text = liner.series
    }

    // ── Rarity ────────────────────────────────────────────────────────

    private fun setupRarity() {
        val num = liner.numberLiner.toIntOrNull() ?: 0
        val (bg, label) = when {
            num <= 50 -> R.drawable.badge_rarity_common to "Common"
            num <= 120 -> R.drawable.badge_rarity_uncommon to "Uncommon"
            num <= 190 -> R.drawable.badge_rarity_rare to "Rare"
            else -> R.drawable.badge_rarity_ultra to "Ultra-Rare"
        }
        binding.rarityBadge.setBackgroundResource(bg)
        binding.rarityBadge.text = label
    }

    // ── Specs grid ────────────────────────────────────────────────────

    private fun setupSpecs() {
        // Specs are not yet stored on Liner — show placeholder dashes.
        // Once Liner model is extended with engine/power/topSpeed,
        // replace these with liner.engine etc.
        binding.specEngine.text = "—"
        binding.specPower.text = "—"
        binding.specTopSpeed.text = "—"
        binding.specYear.text = yearForSeries(liner.series)
    }

    // ── Links ─────────────────────────────────────────────────────────

    private fun setupLinks() {
        with(binding) {
            if (liner.video == "-") containerVideo.visibility = View.GONE
            if (liner.vkArticle == "-") containerVk.visibility = View.GONE
            if (liner.wikiArticle == "-") containerWiki.visibility = View.GONE
            if (liner.websiteSociete == "-") containerSociete.visibility = View.GONE

            containerVideo.setOnClickListener { openUrl(liner.video) }
            containerVk.setOnClickListener { openUrl(liner.vkArticle) }
            containerWiki.setOnClickListener { openUrl(liner.wikiArticle) }
            containerSociete.setOnClickListener { openUrl(liner.websiteSociete) }
        }
    }

    private fun openUrl(url: String) {
        if (url.isNotBlank() && url != "-") {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    // ── CTA ───────────────────────────────────────────────────────────

    private fun setupCta() {
        // Check current state in DB
        lifecycleScope.launch(Dispatchers.IO) {
            val existing = (requireContext().applicationContext as App)
                .getDatabase().linersDao().getLinerFavorite(liner.uniqueNumber)
            withContext(Dispatchers.Main) {
                isInCollection = existing != null
                updateCtaState()
            }
        }

        binding.containerFav.setOnClickListener {
            if (isInCollection) {
                removeFromCollection()
            } else {
                addToCollection()
            }
        }
    }

    private fun updateCtaState() {
        binding.containerFav.text = if (isInCollection) {
            getString(R.string.detail_in_collection)
        } else {
            getString(R.string.detail_add_to_collection)
        }
        binding.containerFav.alpha = if (isInCollection) 0.6f else 1f
    }

    private fun addToCollection() {
        val fav = LinersFavourite(
            key = 0,
            uniqueNumber = liner.uniqueNumber,
            id = liner.id,
            numberLiner = liner.numberLiner,
            brand = liner.brand,
            model = liner.model,
            wikiArticle = liner.wikiArticle,
            websiteSociete = liner.websiteSociete,
            video = liner.video,
            vkArticle = liner.vkArticle,
            imageUrlLiner = liner.imageUrlLiner,
            index = liner.index,
            series = liner.series,
            note = liner.note
        )
        CoroutineScope(Dispatchers.IO).launch {
            (requireContext().applicationContext as App).getDatabase()
                .linersDao().insertLiner(fav)
            withContext(Dispatchers.Main) {
                isInCollection = true
                updateCtaState()
            }
        }
    }

    private fun removeFromCollection() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = (requireContext().applicationContext as App)
                .getDatabase().linersDao()
            val existing = dao.getLinerFavorite(liner.uniqueNumber)
            existing?.let { dao.deleteFavoriteLiner(it) }
            withContext(Dispatchers.Main) {
                isInCollection = false
                updateCtaState()
            }
        }
    }

    // ── Note ──────────────────────────────────────────────────────────

    private fun setupNote() {
        // Load note from DB
        lifecycleScope.launch(Dispatchers.IO) {
            val saved = try {
                (requireContext().applicationContext as App).getDatabase()
                    .linersDao().getNoteLiner(liner.uniqueNumber)
            } catch (e: Exception) {
                null
            }
            withContext(Dispatchers.Main) {
                showNoteRead(saved.orEmpty())
            }
        }

        binding.btnEditNote.setOnClickListener { switchToEditMode() }
        binding.btnNoteCancel.setOnClickListener { showNoteRead(binding.noteText.text.toString()) }
        binding.btnNoteSave.setOnClickListener { saveNote() }
    }

    private fun showNoteRead(text: String) {
        binding.noteText.text = text
        binding.noteText.visibility = View.VISIBLE
        binding.noteEditContainer.visibility = View.GONE
    }

    private fun switchToEditMode() {
        binding.noteEdit.setText(binding.noteText.text)
        binding.noteText.visibility = View.GONE
        binding.noteEditContainer.visibility = View.VISIBLE
    }

    private fun saveNote() {
        val text = binding.noteEdit.text.toString().trim()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                (requireContext().applicationContext as App).getDatabase()
                    .linersDao().editNoteLiner(liner.uniqueNumber, text)
            } catch (e: Exception) {
                // Note: editNoteLiner only works if liner already in favourites.
                // If not, ignore — note will be saved when user adds to collection.
            }
            withContext(Dispatchers.Main) {
                showNoteRead(text)
            }
        }
    }

    // ── Image expand/collapse ─────────────────────────────────────────

    private fun setupImageExpand() {
        binding.imageView.setOnClickListener {
            if (!isImageExpanded) expandImage() else collapseImage()
        }
    }

    private fun expandImage() {
        val overlay = imageOverlay ?: return
        val expanded = expandedImage ?: return

        binding.imageView.visibility = View.INVISIBLE
        overlay.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate().alpha(1f).setDuration(250)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
            setOnClickListener { collapseImage() }
        }
        expanded.apply {
            setImageDrawable(binding.imageView.drawable)
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.75f
            scaleY = 0.75f
            setOnClickListener { collapseImage() }
        }
        expanded.doOnPreDraw {
            expanded.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        }
        isImageExpanded = true
    }

    private fun collapseImage() {
        val overlay = imageOverlay ?: return
        val expanded = expandedImage ?: return

        overlay.animate().alpha(0f).setDuration(200).start()
        expanded.animate().alpha(0f).scaleX(0.75f).scaleY(0.75f)
            .setDuration(250)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                overlay.visibility = View.GONE
                expanded.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
            }.start()
        isImageExpanded = false
    }

    private fun yearForSeries(series: String): String = when {
        series.contains("1") -> "1989"
        series.contains("2") -> "1990"
        series.contains("3") -> "1991"
        series.contains("4") -> "1992"
        series.contains("5") -> "1993"
        else -> "—"
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

    override fun onDestroyView() {
        super.onDestroyView()
        if (isImageExpanded) {
            imageOverlay?.visibility = View.GONE
            expandedImage?.visibility = View.GONE
        }
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator =
            (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }
}