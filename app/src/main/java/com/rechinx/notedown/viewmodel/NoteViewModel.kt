package com.rechinx.notedown.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.rechinx.notedown.dao.NoteDao
import com.rechinx.notedown.model.NoteItem
import io.reactivex.Completable
import io.reactivex.Flowable

class NoteViewModel(private val dataSource: NoteDao): ViewModel(){

    private var removal: MutableLiveData<NoteItem> = MutableLiveData()

    fun noteList(): Flowable<List<NoteItem>> {
        return dataSource.getNotes()
    }

    fun insertNote(item: NoteItem): Completable {
        return Completable.fromAction {
            dataSource.insertNote(item)
        }
    }

    fun getNoteById(objectId: Int): Flowable<NoteItem> {
        return dataSource.getNoteById(objectId)
    }

    fun updateNote(item: NoteItem): Completable {
        return Completable.fromAction {
            dataSource.updateNote(item)
        }
    }

    fun removeNotes(notes: List<NoteItem>): Completable {
        return Completable.fromAction {
            dataSource.deleteNotes(notes)
        }
    }

    fun removeNote(note: NoteItem){
        removal.value = note
    }

    fun doRemoveNote(): LiveData<NoteItem> {
        return removal
    }

}