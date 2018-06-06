package com.rechinx.notedown.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.R
import com.rechinx.notedown.base.BaseFragment
import com.rechinx.notedown.model.NoteItem
import com.rechinx.notedown.utils.KeyboardStatusDetector
import com.rechinx.notedown.utils.Utility
import com.rechinx.notedown.utils.VectorDrawableUtils
import com.rechinx.notedown.viewmodel.NoteViewModel
import com.rechinx.notedown.viewmodel.NoteViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import android.content.Intent
import android.text.TextUtils
import com.rechinx.notedown.support.expandtededittext.ExpandedEditText


class EditFragment: BaseFragment() {

    private val TAG = this.javaClass.simpleName

    private lateinit var toolbar: Toolbar
    private lateinit var mFab: FloatingActionButton
    private lateinit var mEdit: ExpandedEditText
    private lateinit var viewModel: NoteViewModel
    private lateinit var viewModelFactory: NoteViewModelFactory

    private var fromObjectId: Int = -1
    private lateinit var fromNoteItem: NoteItem

    private val disposable = CompositeDisposable()

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

        // init ui
        mEdit = view.findViewById(R.id.et_note_item)

        // get note_id from list
        fromObjectId = arguments?.getInt(EXTRA_ID, -1) ?: -1
        if(fromObjectId != -1) {
            disposable.add(viewModel.getNoteById(fromObjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        fromNoteItem = it
                        noteContent = it.detail
                        if(BuildConfig.DEBUG) {
                            Log.d(TAG, "note from database is $noteContent")
                        }
                        mEdit.fromHtml(noteContent)
                    }, {Log.e(TAG, "Unable to get noteitem")}))
        }else {
            mEdit.fromHtml("")
        }

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
                        mEdit.setCursorVisible(keyboardVisible)
                    }
                })
    }


    fun saveNote() {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "enter the saving notes function")
            Log.d(TAG, "Edit text: " + mEdit.toHtml())
        }
        if(fromObjectId == -1) {
            if(!TextUtils.isEmpty(mEdit.toHtml()) && mEdit.toHtml() != "\n") {
                val item = NoteItem(mEdit.getTitle(), mEdit.toHtml(), System.currentTimeMillis(), System.currentTimeMillis())
                disposable.add(viewModel.insertNote(item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, { error -> Log.e(TAG, "Unable to insert note $error")}))
            }
        }else {
            var item = fromNoteItem
            if(item.detail != mEdit.toHtml()) {
                item.title = mEdit.getTitle()
                item.detail = mEdit.toHtml()
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
                //mEdit.redo()
            }
            R.id.menu_edit_undo -> {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "now is undo operation")
                }
                //mEdit.undo()
            }
            R.id.menu_edit_pic -> insertImage()
        }
        return super.onOptionsItemSelected(item)
    }

    fun insertImage() {
        val getImage = Intent(Intent.ACTION_GET_CONTENT)
        getImage.addCategory(Intent.CATEGORY_OPENABLE)
        getImage.type = "image/*"
        startActivityForResult(getImage, REQUEST_CODE_GET_CONTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data?.data == null)
            return;
        val uri = data.getData()
        val width = mEdit.measuredWidth - mEdit.paddingLeft - mEdit.paddingRight
        mEdit.insertImage(uri)
    }

    companion object {
        private val EXTRA_ID = "note_id"
        private val REQUEST_CODE_GET_CONTENT = 666

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