package com.rechinx.notedown.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.graphics.drawable.VectorDrawableCompat
import com.rechinx.notedown.R

object VectorDrawableUtils {

    fun getMenuDrawable(context: Context?): Drawable? {
        return VectorDrawableCompat.create(context!!.resources, R.drawable.ic_menu_white_24dp, context.theme)
    }

    fun getBackDrawable(context: Context?): Drawable? {
        return VectorDrawableCompat.create(context!!.resources, R.drawable.ic_arrow_back_white_24dp, context.theme)
    }
}