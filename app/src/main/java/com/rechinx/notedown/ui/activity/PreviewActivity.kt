package com.rechinx.notedown.ui.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.Constants
import com.rechinx.notedown.R
import com.rechinx.notedown.support.glide.GlideApp
import com.rechinx.notedown.support.glide.GlideRequests
import com.rechinx.notedown.utils.Utility
import com.rechinx.notedown.utils.VectorDrawableUtils
import kotlinx.android.synthetic.main.activity_preview.*
import java.util.regex.Pattern

class PreviewActivity: AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    private var noteContent: String = ""

    private lateinit var layoutRoot: View
    private lateinit var linearLayout: LinearLayout
    private lateinit var glideRequests: GlideRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        // globals
        glideRequests = GlideApp.with(this)

        // get data
        noteContent = intent.getStringExtra(Constants.EXTRA_CONTENT)
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "data from upper is: $noteContent")
        }
        // setup view
        layoutRoot = findViewById(R.id.layout_root)
        linearLayout = findViewById(R.id.container_layout)

        // init toolbar
        preview_toolbar.title = "预览"
        preview_toolbar.navigationIcon = VectorDrawableUtils.getBackDrawable(this)
        setSupportActionBar(preview_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        calcText()
    }

    private fun calcText() {
        val pattern = Pattern.compile(Constants.IMAGE_REGEX)
        var tmp = noteContent
        while(true) {
            var matcher = pattern.matcher(tmp)
            if(!matcher.find()) break

            val st = matcher.start()
            val ed = matcher.end()

            if(st > 0) {
                addTextView(tmp.substring(0, st))
            }

            var imgStr = tmp.substring(st, ed)
            addImageView(imgStr)
            tmp = tmp.substring(ed)
        }
        if(!tmp.isEmpty()) {
            addTextView(tmp)
        }
    }

    private fun addTextView(content: String) {
        var layoutInflater = LayoutInflater.from(this)
        var viewHolder = PreviewViewHolder(layoutInflater.inflate(R.layout.item_list_preview, null))
        viewHolder.tv_line.visibility = View.VISIBLE
        viewHolder.imgLayout.visibility = View.GONE
        viewHolder.tv_line.text = content
        linearLayout.addView(viewHolder.rootLayout)
    }

    private fun addImageView(imgStr: String) {
        var layoutInflater = LayoutInflater.from(this)
        val uriIndexStart = imgStr.indexOf("src=")
        val uriIndexEnd = imgStr.indexOf("/>")
        val uriStr = imgStr.substring(uriIndexStart + 5, uriIndexEnd - 1)
        var viewHolder = PreviewViewHolder(layoutInflater.inflate(R.layout.item_list_preview, null))
        viewHolder.tv_line.visibility = View.GONE
        viewHolder.imgLayout.visibility = View.VISIBLE
        glideRequests.asBitmap()
                .load(Uri.parse(uriStr))
                .centerCrop()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val imageHeight = Utility.calcImageHeight(resource.height, resource.width)
                        viewHolder.imageView.setImageBitmap(resource)
                        viewHolder.imageView.layoutParams.height = imageHeight
                    }
                })
        linearLayout.addView(viewHolder.rootLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
    class PreviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView = itemView.findViewById(R.id.iv_img_item)
        var tv_line: TextView = itemView.findViewById(R.id.tv_line)
        var rootLayout: View = itemView.findViewById(R.id.root_layout)
        var imgLayout: View = itemView.findViewById(R.id.layout_img)
    }
}