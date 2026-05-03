package com.vshum.turbogum.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.dao.LinersDao
import com.vshum.turbogum.databinding.FragmentNotesBinding
import com.vshum.turbogum.model.LinersFavourite
import com.vshum.turbogum.navigator.AppNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Notes screen — shows all favourite liners that have a non-empty note.
 * Tapping a row opens the inline edit card at the top.
 */
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var appDao: LinersDao

    private val notes = ArrayList<LinersFavourite>()
    private lateinit var adapter: NotesAdapter

    // Currently editing item
    private var editingItem: LinersFavourite? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupEditCard()
        loadNotes()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    // ── RecyclerView ──────────────────────────────────────────────────

    private fun setupRecycler() {
        adapter = NotesAdapter(notes) { item -> openEditCard(item) }
        binding.recyclerNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@NotesFragment.adapter
        }
    }

    // ── Edit card ─────────────────────────────────────────────────────

    private fun setupEditCard() {
        binding.btnEditCancel.setOnClickListener {
            binding.editCard.visibility = View.GONE
            editingItem = null
        }
        binding.btnEditSave.setOnClickListener { saveNote() }
    }

    private fun openEditCard(item: LinersFavourite) {
        editingItem = item
        binding.editCardBrand.text = item.brand
        binding.editCardModel.text = item.model
        binding.editCardInput.setText(item.note)
        binding.editCardInput.setSelection(item.note.length)

        // Load image
        if (item.imageUrlLiner.isNotEmpty()) {
            Picasso.get().load(item.imageUrlLiner).into(binding.editCardImage)
        }

        binding.editCard.visibility = View.VISIBLE
        // Scroll to top
        binding.root.post {
            (binding.root as? androidx.core.widget.NestedScrollView)?.smoothScrollTo(0, 0)
        }
    }

    private fun saveNote() {
        val item = editingItem ?: return
        val text = binding.editCardInput.text.toString().trim()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appDao.editNoteLiner(item.uniqueNumber, text)
            } catch (e: Exception) { /* ignore */ }
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                binding.editCard.visibility = View.GONE
                editingItem = null
                loadNotes()
            }
        }
    }

    // ── Data ──────────────────────────────────────────────────────────

    private fun loadNotes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val all = try {
                appDao.getAllFavouriteLiners()
            } catch (e: Exception) { emptyList() }

            // Only items with non-empty notes
            val withNotes = all.filter { it.note.isNotBlank() }

            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                notes.clear()
                notes.addAll(withNotes)
                adapter.notifyDataSetChanged()

                val count = withNotes.size
                binding.notesCountLabel.text =
                    if (count > 0) "ВСЕ ЗАМЕТКИ ($count)" else "ЗАМЕТКИ"
                binding.emptyState.visibility  = if (count == 0) View.VISIBLE else View.GONE
                binding.recyclerNotes.visibility = if (count > 0) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator = app.servicesLocator.providerNavigator(requireActivity())
        appDao       = app.getDatabase().linersDao()
    }

    // ── Adapter ───────────────────────────────────────────────────────

    inner class NotesAdapter(
        private val list: List<LinersFavourite>,
        private val onTap: (LinersFavourite) -> Unit
    ) : RecyclerView.Adapter<NotesAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView  = view.findViewById(R.id.noteImage)
            val model: TextView   = view.findViewById(R.id.noteModel)
            val preview: TextView = view.findViewById(R.id.notePreview)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note, parent, false)
        )

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.model.text   = item.model
            holder.preview.text = item.note
            if (item.imageUrlLiner.isNotEmpty()) {
                Picasso.get().load(item.imageUrlLiner).into(holder.image)
            }
            holder.itemView.setOnClickListener { onTap(item) }
        }
    }
}