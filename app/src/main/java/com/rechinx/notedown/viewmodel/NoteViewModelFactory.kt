package com.rechinx.notedown.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.rechinx.notedown.dao.NoteDao

class NoteViewModelFactory(private val datasource: NoteDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}