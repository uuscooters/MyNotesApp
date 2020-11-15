package helper

import android.database.Cursor
import db.DatabaseContract
import entity.Note

object MappingHelper {

    fun mapCursorToArrayList(notCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                notesList.add(Note(id, title, description, date))
            }
        }
        return notesList
    }


}