package com.rechinx.notedown.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.rechinx.notedown.App

object ScreenUtils {

    val deviceWidth: Int
        get() {
            val metrics = DisplayMetrics()
            val wm = App.instance().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    fun dpToPx(valueInDp: Float): Float {
        val metrics = App.instance().getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

}
