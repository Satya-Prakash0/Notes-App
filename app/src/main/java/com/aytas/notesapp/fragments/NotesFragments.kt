package com.aytas.notesapp.fragments

import android.content.SharedPreferences.Editor
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aytas.notesapp.MainActivity
import com.aytas.notesapp.R
import com.aytas.notesapp.adapter.RvNotesAdapter
import com.aytas.notesapp.databinding.ActivityMainBinding
import com.aytas.notesapp.databinding.FragmentNotesFragmentsBinding
import com.aytas.notesapp.utils.SwipeToDelete
import com.aytas.notesapp.utils.hideKeyboard
import com.aytas.notesapp.viewModel.NoteMainViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotesFragments : Fragment(R.layout.fragment_notes_fragments) {

    private lateinit var noteBinding: FragmentNotesFragmentsBinding
    private val noteMainViewModel:NoteMainViewModel by activityViewModels()
    private lateinit var rvNotesAdapter: RvNotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition=MaterialElevationScale(false).apply {
            duration=200
        }
        enterTransition=MaterialElevationScale(true).apply {
            duration=200
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteBinding= FragmentNotesFragmentsBinding.bind(view)
        val activity=activity as MainActivity
        val navController= Navigation.findNavController(view)
        requireView().hideKeyboard()

        CoroutineScope(Dispatchers.Main).launch {
//            delay(10)
//            activity.window.statusBarColor=Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS )
            activity.window.statusBarColor=Color.parseColor("#9E9D9D")
        }

        noteBinding.addNoteFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            navController.navigate(NotesFragmentsDirections.actionNotesFragmentsToSaveUpdateFragments())
        }

        noteBinding.innerFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            navController.navigate(NotesFragmentsDirections.actionNotesFragmentsToSaveUpdateFragments())
        }

        recyclerViewDisplay()
        swipeToDelete(noteBinding.rvNote)

        //search implementation
        noteBinding.search.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                noteBinding.noData.isVisible=false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    val text=s.toString()
                    val query="%$text%"
                    if(query.isNotEmpty()){
                        noteMainViewModel.searchNote(query).observe(viewLifecycleOwner){
                              rvNotesAdapter.submitList(it)
                        }
                    }else{
//                        observerDataChanges()
                    }
                }else{
//                    observerDataChanges()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        noteBinding.search.setOnEditorActionListener{v,actionId,_ ->
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        noteBinding.rvNote.setOnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->

            when{
                scrollY>oldScrollY -> {noteBinding.innerFabText.isVisible=false}

                scrollX==scrollY -> {noteBinding.innerFabText.isVisible=true}

                else -> {noteBinding.innerFabText.isVisible=true}
            }
        }

    }

    private fun swipeToDelete(rvNote: RecyclerView) {
        val swipeToDeleteCall= object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.absoluteAdapterPosition
                val note=rvNotesAdapter.currentList[position]
                var actionBtnTapped=false
                noteMainViewModel.deleteNote(note)
                noteBinding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if(noteBinding.search.text.toString().isEmpty()){
                    observerDataChanges()
                }
                val snackbar=Snackbar.make(requireView(),"Note Deleted",Snackbar.LENGTH_LONG)
                    .addCallback(object :BaseTransientBottomBar.BaseCallback<Snackbar>(){
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                        }

                        override fun onShown(transientBottomBar: Snackbar?) {
                            transientBottomBar?.setAction("UNDO"){
                                noteMainViewModel.addNote(note)
                                actionBtnTapped=true
                                noteBinding.noData.isVisible=false
                            }

                            super.onShown(transientBottomBar)
                        }
                    }).apply {
                        animationMode=Snackbar.ANIMATION_MODE_FADE
                        setAnchorView(R.id.add_note_fab)
                    }
                snackbar.setActionTextColor(ContextCompat.getColor(requireContext(),R.color.yellowOrange))
                snackbar.show()

            }
        }
        val itemTouchHelper=ItemTouchHelper(swipeToDeleteCall)
        itemTouchHelper.attachToRecyclerView(rvNote)

    }

    private fun observerDataChanges() {
        noteMainViewModel.getAllNote().observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            rvNotesAdapter.submitList(list)
        }
    }

    private fun recyclerViewDisplay() {
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE-> setUpRecyclerView(3)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {

        noteBinding.rvNote.apply {
            layoutManager=StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            rvNotesAdapter= RvNotesAdapter()
            rvNotesAdapter.stateRestorationPolicy=
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter=rvNotesAdapter
            postponeEnterTransition(300L,TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()
    }
}