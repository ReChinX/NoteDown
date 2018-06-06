package com.rechinx.notedown.support.expandtededittext

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import com.rechinx.notedown.R
import android.R.attr.bitmap
import android.graphics.Bitmap



class DataImageView: ImageView {

    var uri: Uri? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): super(context, attrs, defStyleAttr)

}