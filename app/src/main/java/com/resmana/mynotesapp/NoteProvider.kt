package com.resmana.mynotesapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.resmana.consumer.db.DatabaseContract.AUTHORITY
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.resmana.consumer.db.NoteHelper

class NoteProvider : ContentProvider() {

    companion object {
        /*
        * Integer digunakan sebagai identifier antara select all dengan select by Id
        */

        private const val NOTE = 1
        private const val NOTE_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var noteHelper: NoteHelper

        /*
        * Uri matcher untuk mempermudah identifier dgn menggunakan Integer
        * MISAL :
        * uri com.resmana.mynotesapp dicocockan dgn integer 1
        * uri com.resmana.mynotesapp dicocockan dgn integer 2
        * */

        init {
            // content://com.resmana.mynotesapp/note
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE)

            // conten://com.resmana.mynotesapp/note/id
            sUriMatcher.addURI(
                    AUTHORITY,
                    "$TABLE_NAME/#",
                    NOTE_ID
            )

        }
    }

    override fun onCreate(): Boolean {
        noteHelper = NoteHelper.getInstance(context as Context)
        noteHelper.open()
        return true
    }

    /*
    * Method queryAll digunakan ketika ingin menjalankan queryAll select
    * Return cursor
    */
    override fun query(uri: Uri, string: Array<String>?, s: String?, string1: Array<String>?, s1: String?): Cursor? {
        val cursor: Cursor?
        when (sUriMatcher.match(uri)) {
            NOTE -> cursor = noteHelper.queryALL()
            NOTE_ID -> cursor = noteHelper.queryById(uri.lastPathSegment.toString())
            else -> cursor = null
        }

        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (NOTE) {
            sUriMatcher.match(uri) -> noteHelper.insert(contentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(uri: Uri, contentValues: ContentValues?, S: String?,
                        strings: Array<String>?): Int {
        val update: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.update(uri.lastPathSegment.toString(), contentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return update
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val deleted: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted
    }


    override fun getType(uri: Uri): String? {
        return null
    }

}