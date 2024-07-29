package com.video.offline.videoplayer.database.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteTestDbOpenHelper(context: Context, databaseName: String, version: Int) : SQLiteOpenHelper(context, databaseName, null, version) {

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }
}

