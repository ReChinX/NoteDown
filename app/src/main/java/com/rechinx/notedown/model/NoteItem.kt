package com.rechinx.notedown.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class NoteItem{

    @PrimaryKey(autoGenerate = true)
    var objectId: Int? = null
    @ColumnInfo(name = "created_at")
    var createdAt: Long? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long? = null
    @ColumnInfo(name = "title")
    var title: String?= null
    @ColumnInfo(name = "detail")
    var detail: String?= null

    constructor() {}

    constructor(title: String, detail: String, createdAt: Long, updatedAt: Long) {
        this.objectId = objectId
        this.title = title
        this.detail = detail
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    constructor(objectId: Int, title: String, detail: String, createdAt: Long, updatedAt: Long) {
        this.objectId = objectId
        this.title = title
        this.detail = detail
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}