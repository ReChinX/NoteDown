package com.rechinx.notedown.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.SparseArray
import android.view.*
import com.goyourfly.multiple.adapter.MultipleAdapter
import com.goyourfly.multiple.adapter.MultipleSelect
import com.goyourfly.multiple.adapter.menu.SimpleDeleteMenuBar
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.R
import com.rechinx.notedown.model.NoteItem
import com.rechinx.notedown.ui.activity.MainActivity
import com.rechinx.notedown.ui.adapter.NoteAdapter
import com.rechinx.notedown.utils.Utility
import com.rechinx.notedown.utils.VectorDrawableUtils
import com.rechinx.notedown.viewmodel.NoteViewModel
import com.rechinx.notedown.viewmodel.NoteViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import io.reactivex.disposables.CompositeDisposable


class MainFragment: Fragment() {

    private val TAG = this.javaClass.simpleName

    private lateinit var adapter: NoteAdapter
    private lateinit var mFab: FloatingActionButton
    private lateinit var mView: View
    private var data = ArrayList<NoteItem>()
    private lateinit var viewModel: NoteViewModel
    private lateinit var viewModelFactory: NoteViewModelFactory

    private lateinit var toolbar: Toolbar

    private var preset: HashSet<Int> = HashSet()

    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = Utility.provideViewModelFactory(activity!!)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(NoteViewModel::class.java)
        getData()
        setupUI()
    }

    private fun getData() {
        disposable.add(viewModel.noteList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    data.clear()
                    data = it as ArrayList<NoteItem>
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "item number is: " + data.size)
                    }
                    adapter.setData(data)
                    adapter.notifyItemRangeChanged(0, data.size)
                }, { error -> Log.e(TAG, "Unable to get notes list" + error)}))
    }

    private fun setupUI() {
        toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
        activity!!.title = "Note"
        toolbar.navigationIcon = VectorDrawableUtils.getMenuDrawable(context)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NoteAdapter(data, context!!)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onLongClick(view: View, adapterPosition: Int) {
                (activity as MainActivity).startActionMode()
            }

            override fun onClick(view: View, adapterPosition: Int) {
                if((activity as MainActivity).isActionModeStart()) {
                    addOrRemove(adapterPosition)
                }else {
                    val objectId = data.get(adapterPosition).objectId
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "Current item id is: $objectId")
                    }
                    if (objectId != null) {
                        (activity as MainActivity).changeToEditFragment(objectId)
                    }
                }
            }

        })
        // always shows fab
        mFab = activity!!.findViewById<FloatingActionButton>(R.id.fab)
        mFab.visibility = View.VISIBLE
    }

    private fun addOrRemove(adapterPosition: Int) {
        adapter.addOrRemovePreset(adapterPosition)
        if(preset.contains(adapterPosition)) {
            preset.remove(adapterPosition)
        }else {
            preset.add(adapterPosition)
        }
        if(preset.size == 0) {
            (activity as MainActivity).finishActionMode()
        }else {
            (activity as MainActivity).setActionTitle(preset.size)
        }
    }

    fun clearPreset() {
        adapter.clearPreset()
        preset.clear()
    }

    fun doDelete() {
        var backupList = ArrayList<NoteItem>()
        for (s in preset) {
            backupList.add(data.get(s))
        }
        data.removeAll(backupList)
        adapter.setData(data)
        adapter.notifyItemRangeChanged(0, data.size)
        disposable.add(viewModel.removeNotes(backupList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {error -> Log.d(TAG, "Unable to delete notes $error")}))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    companion object {
        private var instance: MainFragment? = null

        fun newInstance(): MainFragment {
            if(instance == null) {
                instance = MainFragment()
            }
            return instance!!
        }
    }
}

