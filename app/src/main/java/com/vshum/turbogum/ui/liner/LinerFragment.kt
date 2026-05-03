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
 * Hero image → brand/model/number/series tag → CTA → links → collapsible note.
 * Specs section removed (data not available in model).
 */
class LinerFragment(var liner: Liner) : Fragment() {

    private var _binding: FragmentLinerBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private var isImageExpanded = false
    private var isInCollection = false
    private var isNoteExpanded = false

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

        // Fix #1: hide expandedImage overlay when entering screen
        imageOverlay?.visibility = View.GONE
        expandedImage?.visibility = View.GONE

        setupHeader()
        setupHero()
        setupTextFields()
        setupRarity()
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
        header.findViewById<TextView>(R.id.headerTitle)?.text = ""
    }

    // ── Hero image ────────────────────────────────────────────────────

    private fun setupHero() {
        if (liner.imageUrlLiner.isNotEmpty()) {
            Picasso.get()
                .load(liner.imageUrlLiner)
                .into(binding.imageView)
        }
    }

    // ── Text fields ───────────────────────────────────────────────────

    private fun setupTextFields() {
        binding.linerBrand.text = liner.brand
        binding.linerModel.text = liner.model
        binding.linerNumber.text = "#${liner.numberLiner}"
        binding.tagSeries.text = liner.series
    }

    // ── Rarity badge ──────────────────────────────────────────────────

    private fun setupRarity() {
        val num = liner.numberLiner.toIntOrNull() ?: 0
        val series = liner.series.trim()
        val (bg, label) = when {
            series.startsWith("Sport") || series.startsWith("Classic") -> when {
                num <= 18 -> R.drawable.badge_rarity_common   to "Common"
                num <= 35 -> R.drawable.badge_rarity_uncommon to "Uncommon"
                num <= 52 -> R.drawable.badge_rarity_rare     to "Rare"
                else      -> R.drawable.badge_rarity_ultra    to "Ultra"
            }
            else -> when {
                num <= 50  -> R.drawable.badge_rarity_common   to "Common"
                num <= 120 -> R.drawable.badge_rarity_uncommon to "Uncommon"
                num <= 190 -> R.drawable.badge_rarity_rare     to "Rare"
                else       -> R.drawable.badge_rarity_ultra    to "Ultra"
            }
        }
    }

    // ── Links ─────────────────────────────────────────────────────────

    private fun setupLinks() {
        with(binding) {
            if (liner.vkArticle == "-")       containerVk.visibility      = View.GONE
            if (liner.websiteSociete == "-")  containerSociete.visibility = View.GONE
            if (liner.video == "-")            containerVideo.visibility   = View.GONE
            if (liner.wikiArticle == "-")     containerWiki.visibility    = View.GONE

            containerVk.setOnClickListener      { openUrl(liner.vkArticle) }
            containerSociete.setOnClickListener { openUrl(liner.websiteSociete) }
            containerVideo.setOnClickListener   { openUrl(liner.video) }
            containerWiki.setOnClickListener    { openUrl(liner.wikiArticle) }
        }
    }

    private fun openUrl(url: String) {
        if (url.isNotBlank() && url != "-") {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    // ── Note (collapsible) ────────────────────────────────────────────

    private fun setupNote() {
        // Load saved note from DB
        lifecycleScope.launch(Dispatchers.IO) {
            val saved = try {
                (requireContext().applicationContext as App).getDatabase()
                    .linersDao().getNoteLiner(liner.uniqueNumber)
            } catch (e: Exception) { null }
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                showNoteRead(saved.orEmpty())
            }
        }

        // Toggle expand/collapse on header tap
        binding.noteHeader.setOnClickListener { toggleNote() }

        // Tap on read-mode text → switch to edit
        binding.noteText.setOnClickListener { switchToEditMode() }

        binding.btnNoteCancel.setOnClickListener {
            showNoteRead(binding.noteText.text.toString())
        }
        binding.btnNoteSave.setOnClickListener { saveNote() }
    }

    private fun toggleNote() {
        isNoteExpanded = !isNoteExpanded
        binding.noteBodyContainer.visibility = if (isNoteExpanded) View.VISIBLE else View.GONE
        // Rotate chevron: 90° = pointing down (expanded), 0° = pointing right (collapsed)
        binding.noteChevron.animate()
            .rotation(if (isNoteExpanded) 270f else 90f)
            .setDuration(200)
            .start()
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
            } catch (e: Exception) { /* only works if liner already in favourites */ }
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                showNoteRead(text)
            }
        }
    }

    // ── CTA ───────────────────────────────────────────────────────────

    private fun setupCta() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = (requireContext().applicationContext as App).getDatabase()
            isInCollection = try {
                db.linersDao().getLinerFavorite(liner.uniqueNumber) != null
            } catch (e: Exception) { false }
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                updateCtaState()
            }
        }

        binding.containerFav.setOnClickListener {
            if (isInCollection) removeFromCollection() else addToCollection()
        }
    }

    private fun updateCtaState() {
        binding.containerFav.apply {
            text = if (isInCollection)
                getString(R.string.detail_in_collection)
            else
                getString(R.string.detail_add_to_collection)
            setBackgroundResource(
                if (isInCollection) R.drawable.btn_violet_gradient
                else R.drawable.btn_cta_gradient
            )
        }
    }

    private fun addToCollection() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fav = LinersFavourite(
                    key          = 0, // autoGenerate — Room assigns the real key
                    uniqueNumber = liner.uniqueNumber,
                    id           = liner.id,
                    numberLiner  = liner.numberLiner,
                    brand        = liner.brand,
                    model        = liner.model,
                    wikiArticle  = liner.wikiArticle,
                    websiteSociete = liner.websiteSociete,
                    video        = liner.video,
                    vkArticle    = liner.vkArticle,
                    imageUrlLiner = liner.imageUrlLiner,
                    index        = liner.index,
                    series       = liner.series,
                    note         = ""
                )
                (requireContext().applicationContext as App)
                    .getDatabase().linersDao().insertLiner(fav)
                isInCollection = true
            } catch (e: Exception) { /* ignore */ }
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                updateCtaState()
            }
        }
    }

    private fun removeFromCollection() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = (requireContext().applicationContext as App).getDatabase()
                val fav = db.linersDao().getLinerFavorite(liner.uniqueNumber)
                if (fav != null) db.linersDao().deleteFavoriteLiner(fav)
                isInCollection = false
            } catch (e: Exception) { /* ignore */ }
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                updateCtaState()
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