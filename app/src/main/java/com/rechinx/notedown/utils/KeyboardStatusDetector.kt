package com.rechinx.notedown.utils

import android.app.Activity
import android.graphics.Rect
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewTreeObserver

class KeyboardStatusDetector {
    private var mVisibilityListener: KeyboardVisibilityListener? = null

    internal var keyboardVisible = false

    fun registerFragment(f: Fragment) {
        registerView(f.getView()!!)
    }

    fun registerActivity(a: Activity) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content))
    }

    fun registerView(v: View): KeyboardStatusDetector {
        v.getViewTreeObserver().addOnGlobalLayoutListener {
            val r = Rect()
            v.getWindowVisibleDisplayFrame(r)

            val heightDiff = v.getRootView().getHeight() - (r.bottom - r.top)
            if (heightDiff > ScreenUtils.dpToPx(SOFT_KEY_BOARD_MIN_HEIGHT.toFloat())) { // if more than 100 pixels, its probably a keyboard...
                if (!keyboardVisible) {
                    keyboardVisible = true
                    if (mVisibilityListener != null) {
                        mVisibilityListener!!.onVisibilityChanged(true)
                    }
                }
            } else {
                if (keyboardVisible) {
                    keyboardVisible = false
                    if (mVisibilityListener != null) {
                        mVisibilityListener!!.onVisibilityChanged(false)
                    }
                }
            }
        }

        return this
    }

    fun setmVisibilityListener(listener: KeyboardVisibilityListener): KeyboardStatusDetector {
        mVisibilityListener = listener
        return this
    }

    interface KeyboardVisibilityListener {
        fun onVisibilityChanged(keyboardVisible: Boolean)
    }

    companion object {
        private val SOFT_KEY_BOARD_MIN_HEIGHT = 100
    }

}


