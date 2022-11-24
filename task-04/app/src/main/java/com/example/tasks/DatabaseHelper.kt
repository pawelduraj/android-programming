package com.example.tasks

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " (" +
                NAME_COL + " TEXT, " +
                DESC_COL + " TEXT," +
                DATE_COL + " TEXT," +
                STATUS_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(name: String, desc: String, date: String, status: String) {
        val values = ContentValues()
        values.put(NAME_COL, name)
        values.put(DESC_COL, desc)
        values.put(DATE_COL, date)
        values.put(STATUS_COL, status)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getTasks(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun removeTask(name: String, desc: String, date: String, status: String) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME WHERE $NAME_COL = \"$name\" AND $DESC_COL = \"$desc\" AND $DATE_COL = \"$date\" AND $STATUS_COL = \"$status\"")
    }

    companion object {
        private const val DATABASE_NAME = "list"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "tasks"
        const val NAME_COL = "name"
        const val DESC_COL = "desc"
        const val DATE_COL = "date"
        const val STATUS_COL = "status"
    }
}
