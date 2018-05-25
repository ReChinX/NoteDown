package com.rechinx.notedown.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.rechinx.notedown.R
import com.rechinx.notedown.utils.VectorDrawableUtils

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if(toolbar != null) {
            toolbar.navigationIcon = VectorDrawableUtils.getMenuDrawable(this)
            setSupportActionBar(toolbar)
        }
    }

    protected abstract fun getLayoutId(): Int
}