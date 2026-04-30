package com.vshum.turbogum.ui.favorite_liner

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
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentFavoriteLinerBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Detail screen for a sticker that's already in the user's favourites.
 * Differs from LinerFragment in that the CTA is "Remove from collection"
 * (the sticker is already saved) and the heart badge is filled by default.
 *
 * Sections: hero with badges → brand/model/number → links 2x2 → note (read+edit)
 * → remove button.
 */
class FavoriteLinerFragment(var linerFav: LinersFavourite) : Fragment() {

    private var _binding: FragmentFavoriteLinerBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDao: LinersDao
    private lateinit var appNavigator: AppNavigator
    private var isImageExpanded = false

    private val imageOverlay: View?
        get() = activity?.findViewById(R.id.imageOverlay)
    private val expandedImage: ImageView?
        get() = activity?.findViewById(R.id.expandedImage)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteLinerBinding.inflate(inflater, container, false)
        appDao = (requireContext().applicationContext as App).getDatabase().linersDao()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupHero()
        setupTextFields()
        setupLinks()
        setupNote()
        setupImageExpand()
        setupRemoveButton()
    }

    // ── Header ────────────────────────────────────────────────────────

    private fun setupHeader() {
        val header = binding.headerInclude.root
        header.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            appNavigator.navigateTo(Screen.FAVOURITE)
        }
        header.findViewById<TextView>(R.id.headerTitle).text = ""
    }

    // ── Hero image ────────────────────────────────────────────────────

    private fun setupHero() {
        if (linerFav.imageUrlLiner.isNotEmpty()) {
            Picasso.get().load(linerFav.imageUrlLiner).into(binding.imageView)
        }
        binding.linerIndex.text = linerFav.index
        binding.yearTag.text = yearForSeries(linerFav.series)
    }

    // ── Text fields ───────────────────────────────────────────────────

    private fun setupTextFields() {
        binding.linerBrand.text = linerFav.brand
        binding.linerModel.text = linerFav.model
        binding.linerNumber.text = "#${linerFav.numberLiner}"
    }

    // ── Links ─────────────────────────────────────────────────────────

    private fun setupLinks() {
        with(binding) {
            if (linerFav.video == "-") linkVideo.visibility = View.GONE
            if (linerFav.vkArticle == "-") linkVk.visibility = View.GONE
            if (linerFav.wikiArticle == "-") linkWiki.visibility = View.GONE
            if (linerFav.websiteSociete == "-") websiteSociete.visibility = View.GONE

            linkVideo.setOnClickListener { openLink(linerFav.video) }
            linkVk.setOnClickListener { openLink(linerFav.vkArticle) }
            linkWiki.setOnClickListener { openLink(linerFav.wikiArticle) }
            websiteSociete.setOnClickListener { openLink(linerFav.websiteSociete) }
        }
    }

    private fun openLink(url: String) {
        if (url.isNotBlank() && url != "-") {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    // ── Note (read / edit) ────────────────────────────────────────────

    private fun setupNote() {
        // Load saved note from DB
        lifecycleScope.launch(Dispatchers.IO) {
            val saved = try {
                appDao.getNoteLiner(linerFav.uniqueNumber)
            } catch (e: Exception) {
                null
            }
            withContext(Dispatchers.Main) {
                val text = if (saved.isNullOrBlank() || saved == "-") "" else saved
                showNoteRead(text)
            }
        }

        binding.btnEditNote.setOnClickListener { switchToEditMode() }
        binding.btnNoteCancel.setOnClickListener {
            showNoteRead(binding.noteTxtView.text.toString())
        }
        binding.saveNoteBtn.setOnClickListener { saveNote() }
    }

    private fun showNoteRead(text: String) {
        binding.noteTxtView.text = text
        binding.noteTxtView.visibility = View.VISIBLE
        binding.noteEditContainer.visibility = View.GONE
    }

    private fun switchToEditMode() {
        binding.noteInput.setText(binding.noteTxtView.text)
        binding.noteTxtView.visibility = View.GONE
        binding.noteEditContainer.visibility = View.VISIBLE
    }

    private fun saveNote() {
        val text = binding.noteInput.text.toString().trim()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDao.editNoteLiner(linerFav.uniqueNumber, text)
            } catch (e: Exception) {
                // ignore
            }
            withContext(Dispatchers.Main) {
                showNoteRead(text)
            }
        }
    }

    // ── Remove from collection ────────────────────────────────────────

    private fun setupRemoveButton() {
        binding.btnRemoveFromCollection.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    appDao.deleteFavoriteLiner(linerFav)
                } catch (e: Exception) {
                    // ignore
                }
                withContext(Dispatchers.Main) {
                    appNavigator.navigateTo(Screen.FAVOURITE)
                }
            }
        }
    }

    // ── Image expand / collapse ───────────────────────────────────────

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