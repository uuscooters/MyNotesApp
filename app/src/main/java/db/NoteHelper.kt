package db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import db.DatabaseContract.NoteColumns.Companion._ID
import java.sql.SQLException
import kotlin.jvm.Throws

class NoteHelper (context: Context) {

    private lateinit var database: SQLiteDatabase
    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)

    companion object{
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: NoteHelper? = null

        // Singleton NoteHelper
        fun getInstance(context: Context): NoteHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NoteHelper(context)
            }
    }

    /* Metode connection ke database*/
    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    /* metode disconnection ke database*/
    fun close() {
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    /* Proses CRUD data ke database */

    // Metode CREATE data
    fun insert(values: ContentValues?): Long {
        return database.insert(
            DATABASE_TABLE,
            null,
            values
        )
    }


    // Metode READ All Data
    fun queryALL(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC",
            null
        )
    }

    // Metode READ By ID
    fun queryById(id: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
    }

    // Metode UPDATE data
    fun update(id: String, values: ContentValues?): Int {
        return database.update(
            DATABASE_TABLE,
            values,
            "$_ID = ?",
            arrayOf(id)
        )
    }

    // Metode DELETE data
    fun deleteById(id: String): Int {
        return database.delete(
            DATABASE_TABLE,
            "$_ID = '$id'",
            null)
    }
}