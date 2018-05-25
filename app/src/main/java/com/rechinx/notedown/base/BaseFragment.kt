package com.rechinx.notedown.base

import android.content.Context
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager

open class BaseFragment: Fragment() {

    fun hideKeyboard() {
        // Check if no view has focus:
        val view = activity!!.currentFocus
        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }
    }

    protected fun showKeyboard() {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)//弹出软键盘
    }

}