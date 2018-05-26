package com.rechinx.notedown.ui.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.R
import com.rechinx.notedown.model.NoteItem
import com.rechinx.notedown.ui.activity.MainActivity
import com.rechinx.notedown.utils.FuzzyDateFormatter
import com.rechinx.notedown.utils.VectorDrawableUtils
import kotlinx.android.synthetic.main.list_item_note.view.*

class NoteAdapter(data: List<NoteItem>, context: Context): RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val TAG = this.javaClass.simpleName

    lateinit var mOnItemTouchListener: NoteAdapter.OnItemClickListener
    private lateinit var mData: List<NoteItem>
    private lateinit var mContext: Context
    private var preset: HashSet<Int> = HashSet()

    init {
        this.mData = data
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_note, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData.get(position)
        holder.bind(data, position)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemTouchListener = listener
    }

    fun setData(data: List<NoteItem>) {
        this.mData = data
    }

    fun addOrRemovePreset(position: Int) {
        if(preset.contains(position)) {
            preset.remove(position)
        }else {
            preset.add(position)
        }
        notifyDataSetChanged()
    }

    fun clearPreset() {
        preset.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: NoteItem, position: Int) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "preset is: $preset")
            }
            if(preset.contains(position)) {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "current $position is selected")
                }
                itemView.framelayout_content.isSelected = true
            }else {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "current $position is unselected")
                }
                itemView.framelayout_content.isSelected = false
            }
            itemView.tv_modify_time.text = FuzzyDateFormatter.format(data.updatedAt!!)
            itemView.tv_title.text = data.title
            itemView.setOnClickListener({
                if(mOnItemTouchListener != null) {
                    mOnItemTouchListener.onClick(it, position)
                }
            })
            itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View): Boolean {
                    if(mOnItemTouchListener != null) {
                        mOnItemTouchListener.onLongClick(v, position)
                    }
                    return false
                }
            })
        }
    }

    interface OnItemClickListener {
        fun onClick(view: View, adapterPosition: Int)
        fun onLongClick(view: View, adapterPosition: Int)
    }
}