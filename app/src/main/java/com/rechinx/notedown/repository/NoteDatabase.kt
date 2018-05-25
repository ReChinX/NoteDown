package com.rechinx.notedown.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.rechinx.notedown.Constants
import com.rechinx.notedown.dao.NoteDao
import com.rechinx.notedown.model.NoteItem
import io.reactivex.Flowable
import io.reactivex.Single

@Database(entities = arrayOf(NoteItem::class), version = 1)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        private var INSTANCE: NoteDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): NoteDatabase {
            synchronized(lock) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            NoteDatabase::class.java, Constants.DATABASE_NAME).build()
                }
                return INSTANCE!!
            }
        }
    }
}