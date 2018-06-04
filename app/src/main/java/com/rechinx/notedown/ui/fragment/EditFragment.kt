package com.rechinx.notedown.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.R
import com.rechinx.notedown.base.BaseFragment
import com.rechinx.notedown.model.NoteItem
import com.rechinx.notedown.ui.view.MultiLineDividerEditText
import com.rechinx.notedown.utils.KeyboardStatusDetector
import com.rechinx.notedown.utils.Utility
import com.rechinx.notedown.utils.VectorDrawableUtils
import com.rechinx.notedown.viewmodel.NoteViewModel
import com.rechinx.notedown.viewmodel.NoteViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit.*

class EditFragment: BaseFragment() {

    private val TAG = this.javaClass.simpleName

    private lateinit var toolbar: Toolbar
    private lateinit var mFab: FloatingActionButton
    private lateinit var mEdit: MultiLineDividerEditText
    private lateinit var viewModel: NoteViewModel
    private lateinit var viewModelFactory: NoteViewModelFactory

    private var fromObjectId: Int = -1
    private lateinit var fromNoteItem: NoteItem

    private val disposable = CompositeDisposable()

    private var lastScrollY: Int = 0

    private var noteContent:String ?= "test\n"
    private var menuFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = Utility.provideViewModelFactory(activity!!)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(NoteViewModel::class.java)

        // get note_id from list
        fromObjectId = arguments?.getInt(EXTRA_ID, -1) ?: -1
        if(fromObjectId != -1) {
            disposable.add(viewModel.getNoteById(fromObjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        fromNoteItem = it
                        noteContent = it.detail
                        et_note_item.setText(noteContent)
                    }, {Log.e(TAG, "Unable to get noteitem")}))
        }

        // init ui
        mEdit = view.findViewById(R.id.et_note_item)
        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
        saveNote()
    }
    private fun setupUI() {
        // Toolbar setting
        toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "编辑"
        toolbar.navigationIcon = VectorDrawableUtils.getBackDrawable(context)

        // floating action bar
        mFab = activity!!.findViewById<FloatingActionButton>(R.id.fab)
        mFab.visibility = View.INVISIBLE

        // keyboard detector
        KeyboardStatusDetector().registerView(view!!.rootView)
                .setmVisibilityListener(object : KeyboardStatusDetector.KeyboardVisibilityListener {
                    override fun onVisibilityChanged(keyboardVisible: Boolean) {
                        menuFlag = keyboardVisible
                        activity?.invalidateOptionsMenu()
                        mEdit.isCursorVisible = keyboardVisible
                    }
                })
    }


    fun saveNote() {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "enter the saving notes function")
            Log.d(TAG, "Edit text: " + mEdit.text)
        }
        if(fromObjectId == -1) {
            if(!TextUtils.isEmpty(mEdit.text) && mEdit.text.toString() != "\n") {
                val item = NoteItem(mEdit.text.toString(), mEdit.text.toString(), System.currentTimeMillis(), System.currentTimeMillis())
                disposable.add(viewModel.insertNote(item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, { error -> Log.e(TAG, "Unable to insert note $error")}))
            }
        }else {
            var item = fromNoteItem
            if(item.detail != mEdit.text.toString()) {
                item.title = mEdit.text.toString()
                item.detail = mEdit.text.toString()
                item.updatedAt = System.currentTimeMillis()
                disposable.add(viewModel.updateNote(item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, { error -> Log.e(TAG, "Unable to update note $error")}))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "Enter EditFragment onResume")
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val menuInflater = activity!!.menuInflater
        if(menuFlag) {
            menuInflater.inflate(R.menu.menu_edit, menu)
        }else {
            menuInflater.inflate(R.menu.menu_preview, menu)
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_redo -> {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "now is redo operation")
                }
                mEdit.redo()
            }
            R.id.menu_edit_undo -> {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "now is undo operation")
                }
                mEdit.undo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val EXTRA_ID = "note_id"

        fun newInstance() = EditFragment()

        fun newInstance(objectId: Int): EditFragment {
            var args = Bundle()
            args.putInt(EXTRA_ID, objectId)
            var fragment = EditFragment()
            fragment.setHasOptionsMenu(true)
            fragment.arguments = args
            return fragment
        }
    }

}