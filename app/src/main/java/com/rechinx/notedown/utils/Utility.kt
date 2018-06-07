package com.rechinx.notedown.utils

import android.content.Context
import com.rechinx.notedown.dao.NoteDao
import com.rechinx.notedown.repository.NoteDatabase
import com.rechinx.notedown.viewmodel.NoteViewModelFactory

object Utility {

    fun provideNoteDao(context: Context): NoteDao {
        val database = NoteDatabase.getInstance(context)
        return database.noteDao()
    }

    fun provideViewModelFactory(context: Context): NoteViewModelFactory {
        val dataSource = provideNoteDao(context)
        return NoteViewModelFactory(dataSource)
    }

    fun calcImageHeight(height: Int, width: Int): Int {
        val viewWidth = ScreenUtils.deviceWidth - ScreenUtils.dpToPx(56f)
        return (viewWidth / width * height).toInt()
    }

}