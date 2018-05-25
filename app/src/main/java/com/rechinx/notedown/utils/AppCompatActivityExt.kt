package com.rechinx.notedown.utils

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

/**
 * Runs FragmentTransaction with commit
 */
inline fun FragmentManager.transaction(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply{
        action()
    }.commit()
}

/**
 * AppCompatActivity Extension Function with replace
 */
fun AppCompatActivity.replaceFragmentInActivity(@IdRes frameId: Int, fragment: Fragment) {
    supportFragmentManager.transaction {
        replace(frameId, fragment)
    }
}