package com.rechinx.notedown.dao

import android.arch.persistence.room.*
import com.rechinx.notedown.model.NoteItem
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface NoteDao {

    @Insert
    fun insertNote(note: NoteItem)

    @Update
    fun updateNote(note: NoteItem)

    @Delete
    fun deleteNotes(notes: List<NoteItem>)

    @Query("select * from NoteItem")
    fun getNotes(): Flowable<List<NoteItem>>

    @Query("select * from NoteItem where objectId= :objectId")
    fun getNoteById(objectId: Int): Flowable<NoteItem>
}