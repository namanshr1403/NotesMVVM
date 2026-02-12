package com.example.roomdatabase.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.View

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.roomdatabase.R
import com.example.roomdatabase.activities.MainActivity
import com.example.roomdatabase.databinding.BottomSheetLayoutBinding

import com.example.roomdatabase.databinding.FragmentSaveOrDeleteBinding
import com.example.roomdatabase.model.Note
import com.example.roomdatabase.utils.hideKeyBoard
import com.example.roomdatabase.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveOrDeleteBinding
    private var note: Note? = null
    private lateinit var result:String
    private var color=-1

    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()

    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementEnterTransition = animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding = FragmentSaveOrDeleteBinding.bind(view)

        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclerView${args.note?.id}"
        )

        contentBinding.backButton.setOnClickListener {
            requireView().hideKeyBoard()
            navController.popBackStack()
        }

        contentBinding.saveNote.setOnClickListener {
            saveNote()
        }

            try{
               contentBinding.etNoteContent.setOnFocusChangeListener{ _,hasFocus ->
                   if (hasFocus)
                   {
                       contentBinding.bottomBar.visibility = View.VISIBLE
                       contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                   }else{
                       contentBinding.bottomBar.visibility= View.GONE
                   }

               }
            }
            catch (e:Throwable) {
                Log.d("TAG",e.stackTraceToString())
            }

            contentBinding.fabColorPicker.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(
                    requireContext(),
                    R.style.BottomSheetDialogTheme
                )
                val bottomSheetView: View = layoutInflater.inflate(
                    R.layout.bottom_sheet_layout,
                    null
                )
                with(bottomSheetDialog)
                {
                    setContentView(bottomSheetView)
                    show()
                }


                val  bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
                bottomSheetBinding.apply {
                    colorPicker.apply {
                        setSelectedColor(color)
                        setOnColorSelectedListener {
                            value ->
                            color = value
                            contentBinding.apply {
                                noteContentFragmentParent.setBackgroundColor(color)
                                toolbarFragmentNoteContent.setBackgroundColor(color)
                                bottomBar.setBackgroundColor(color)
                                activity.window.statusBarColor = color
                            }
                            bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                        }
                    }
                    bottomSheetParent.setCardBackgroundColor(color)
                }
                bottomSheetView.post{
                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
//        Open Existing Note
        setUpNote()
    }

    private fun setUpNote() {
        val note=args.note
        val title=contentBinding.etTitle
        val content=contentBinding.etNoteContent
        val lastEdited=contentBinding.lastEdited

        if(note==null)
        {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
            val dateTime = LocalDateTime.now().format(formatter)
            contentBinding.lastEdited.text = getString(R.string.edited_on, dateTime)

        }
        if(note!=null)
        {
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text=getString(R.string.edited_on,note.date)
            color=note.color
            contentBinding.apply {
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

        val title = contentBinding.etTitle.text.toString().trim()
        val content = contentBinding.etNoteContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(activity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            return
        }

        note = args.note

        when (note) {

            null -> {
                // Save new note
                noteActivityViewModel.saveNote(
                    Note(
                        0,
                        title,
                        contentBinding.etNoteContent.getMD(),
                        currentDate,
                        color
                    )
                )
                result = "Note Saved"
                setFragmentResult(
                    "key",
                    bundleOf("bundleKey" to result)
                )

                navController.navigate(
                    SaveOrDeleteFragmentDirections
                        .actionSaveOrDeleteFragmentToNoteFragment()
                )
            }

            else -> {
                // Update existing note
               updateNote()
                navController.popBackStack()
            }
        }
    }

    private fun updateNote() {
        if(note!=null)
        {
            noteActivityViewModel.updateNote(
                Note(
                    note!!.id,
                    contentBinding.etTitle.text.toString(),
                    contentBinding.etNoteContent.getMD(),
                    currentDate,
                    color
                )
            )
        }
    }


}