package com.aytas.notesapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.aytas.notesapp.MainActivity
import com.aytas.notesapp.R
import com.aytas.notesapp.databinding.BottomSheetLayoutBinding
import com.aytas.notesapp.databinding.FragmentSaveUpdateFragmentsBinding
import com.aytas.notesapp.models.Note
import com.aytas.notesapp.utils.hideKeyboard
import com.aytas.notesapp.viewModel.NoteMainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SaveUpdateFragments : Fragment(R.layout.fragment_save_update_fragments) {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSaveUpdateFragmentsBinding
    private var note:Note?=null
    private var color:Int=-1;
    private val noteMainViewModel:NoteMainViewModel by activityViewModels()
    private val currentDate= SimpleDateFormat.getInstance().format(Date())
    private val job= CoroutineScope(Dispatchers.Main)
    private val args:SaveUpdateFragmentsArgs by navArgs()
    private lateinit var result: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animation=MaterialContainerTransform().apply {
            drawingViewId=R.id.fragment
            scrimColor= Color.TRANSPARENT
            duration=200L
        }

        sharedElementEnterTransition=animation
        sharedElementReturnTransition=animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentSaveUpdateFragmentsBinding.bind(view)
        navController=Navigation.findNavController(view)
        val activity=activity as MainActivity

        binding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        ViewCompat.setTransitionName(binding.noteContentFragmentParent,
        "recyclerView_${args.note?.id}"
        )


        binding.saveNoteBtn.setOnClickListener {
            saveNote()
        }

        binding.etNoteContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.bottomBar.visibility = View.VISIBLE
                    binding.etNoteContent.setStylesBar(binding.styleBar)
                } else {
                    binding.bottomBar.visibility = View.GONE
                }
        }



            binding.fabColorPicker.setOnClickListener{
                val bottomSheetDialog=BottomSheetDialog(
                    requireContext(),R.style.BottomSheetDialogTheme
                )

                val bottomSheetView:View=layoutInflater.inflate(R.layout.bottom_sheet_layout,null)
                with(bottomSheetDialog){
                    setContentView(bottomSheetView)
                    show()
                }

                val bottomSheetLayoutBinding=BottomSheetLayoutBinding.bind(bottomSheetView)
                bottomSheetLayoutBinding.apply {
                    colorPicker.apply {
                        setSelectedColor(color)
                        setOnColorSelectedListener {
                            value->
                            color=value
                            binding.apply {
                                noteContentFragmentParent.setBackgroundColor(color)
                                toolbarFragmentNoteContent.setBackgroundColor(color)
                                bottomBar.setBackgroundColor(color)
                                activity.window.statusBarColor=color
                            }

                            bottomSheetLayoutBinding.bottomSheetParent.setCardBackgroundColor(color)
                        }
                    }
                    bottomSheetParent.setCardBackgroundColor(color)
                }
                bottomSheetView.post {
                    bottomSheetDialog.behavior.state=BottomSheetBehavior.STATE_EXPANDED
                }
            }
//open with existing note item
        setUpNote()
    }

    private fun setUpNote() {
        val note=args.note
        val title=binding.etTitle
        val content=binding.etNoteContent
        val lastEdited=binding.noteEdited

        if(note==null){
            binding.noteEdited.text=getString(R.string.edited_on,SimpleDateFormat.getDateInstance().format(Date()))
        }
        if(note!=null){
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text=getString(R.string.edited_on,note.date)
            color=note.color
            binding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor=note.color
        }
    }

    private fun saveNote() {
        if (binding.etNoteContent.text.toString().isEmpty() || binding.etTitle.text.toString().isEmpty()) {
            Toast.makeText(activity, "empty", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
            when (note) {
                null -> {
                    noteMainViewModel.addNote(
                        Note(0, binding.etTitle.text.toString(), currentDate, binding.etNoteContent.getMD(), color)
                    )

                    result="Note Saved"
                    setFragmentResult("key", bundleOf("bundleKey" to result))


                    navController.navigate(SaveUpdateFragmentsDirections.actionSaveUpdateFragmentsToNotesFragments())
                }
                else -> {
                    updateNote()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        if(note!=null){
            noteMainViewModel.updateNote(
                Note(
                    note!!.id,
                    binding.etTitle.text.toString(),
                    binding.etNoteContent.getMD(),
                    currentDate,color
                )
            )
        }
    }

}