package com.rechinx.notedown.ui.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.view.ActionMode
import android.text.BoringLayout
import android.util.Log
import android.util.SparseArray
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.rechinx.notedown.R
import com.rechinx.notedown.base.BaseActivity
import com.rechinx.notedown.ui.fragment.EditFragment
import com.rechinx.notedown.ui.fragment.MainFragment
import com.rechinx.notedown.utils.replaceFragmentInActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: BaseActivity(), NavigationView.OnNavigationItemSelectedListener, ActionMode.Callback {

    private val TAG = this.javaClass.simpleName

    private var actionMode: ActionMode? = null

    private lateinit var mFab: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fragment
        replaceFragmentInActivity(R.id.fragment_container, MainFragment.newInstance())

        // Floating Action Bar
        mFab = findViewById<FloatingActionButton>(R.id.fab)
        mFab.visibility = View.VISIBLE
        mFab.setOnClickListener { view ->
            var fragment = EditFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_scale, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragment.javaClass.getName())
                    .commit()
        }

        // Navigator view
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "" + supportFragmentManager.backStackEntryCount)
                if(supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }else {
                    drawer_layout.openDrawer(GravityCompat.START)
                }
            }
            R.id.action_settings -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeToEditFragment(objectId: Int) {
        var fragment = EditFragment.newInstance(objectId)
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_scale, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.javaClass.getName())
                .commit()
    }

    fun startActionMode() {
        if(actionMode == null) {
            actionMode = startSupportActionMode(this)
        }
    }

    fun isActionModeStart():  Boolean {
        return actionMode != null
    }

    fun setActionTitle(number: Int) {
        actionMode?.title = "Selected $number items"
    }

    fun finishActionMode() {
        actionMode?.finish()
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        MainFragment.newInstance().clearPreset()
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_multi_select_delete -> {
                MainFragment.newInstance().doDelete()
                actionMode?.finish()
                return true
            }
            else -> return false
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        if(!isActionModeStart()) {
            actionMode = mode
            val menuInflater = mode?.menuInflater
            menuInflater?.inflate(R.menu.menu_delete, menu)
            return true
        }else return false
    }
}