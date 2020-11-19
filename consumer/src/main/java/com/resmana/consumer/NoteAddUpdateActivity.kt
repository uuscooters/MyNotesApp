package com.resmana.consumer

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.DATE
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.TITLE
import entity.Note
import helper.MappingHelper
import kotlinx.android.synthetic.main.activity_note_add_update.*
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
//    private lateinit var noteHelper: NoteHelper
    private lateinit var uriWithId: Uri

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_add_update)

//        noteHelper = NoteHelper.getInstance(applicationContext)
//        noteHelper.open()

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            // Uri yg didapatkan disini akan digunakan untuk ambil data dari provider
            // content://com.resmana.mynotesapp/note/id
            uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + note?.id)

            val cursor = contentResolver.query(
                uriWithId,
                null,
                null,
                null,
                null)
            if ( cursor != null) {
                note = MappingHelper.mapCursorToObject(cursor)
                cursor.close()
            }
            actionBarTitle = "Ubah"
            btnTitle = "Update"

            note?.let {
                edt_title.setText(it.title)
                edt_description.setText(it.description)
            }

        } else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_submit.text = btnTitle
        btn_submit.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_submit) {
            val title = edt_title.text.toString().trim()
            val desription = edt_description.text.toString().trim()

            if (title.isEmpty()) {
                edt_title.error = "Field can not be blank"
                return
            }

            val values = ContentValues()
            values.put(TITLE, title)
            values.put(DESCRIPTION, desription)

//            note?.title = title
//            note?.description = desription
//
//            val intent = Intent()
//            intent.putExtra(EXTRA_NOTE, note)
//            intent.putExtra(EXTRA_POSITION, position)


            if (isEdit) {
                // Gunakan uriWithId untuk Update
                // content://com.resmana.mynotesapp/note/id
                contentResolver.update(uriWithId, values, null, null)
                Toast.makeText(this, "Satu Item Berhasil diedit", Toast.LENGTH_SHORT).show()
                finish()

                // Update
//                val result = noteHelper.update(note?.id.toString(), values).toLong()
//                if (result > 0) {
//                    setResult(RESULT_UPDATE, intent)
//                    finish()
//                } else {
//                    Toast.makeText(this@NoteAddUpdateActivity, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
//                }
            } else {
                // Add or insert
//                note?.date = getCurrentDate()
                values.put(DATE, getCurrentDate())

                // Gunakan content uri unutk Insert
                // content://com.resmana.mynotesapp/note
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this, "Satu Item berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()


//                val result = noteHelper.insert(values)
//
//                if (result > 0) {
//                    note?.id = result.toInt()
//                    setResult(RESULT_ADD, intent)
//                    finish()
//                } else {
//                    Toast.makeText(this@NoteAddUpdateActivity, "Gagal menambah data", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Note"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder.setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") {
                dialog, id ->
                if (isDialogClose) {
                    finish()
                    } else {
                    // Gunakan uriWithId dati Intent activity ini
                    // content://com.resmana.mynotesapp/note/id
                    contentResolver.delete(uriWithId, null, null)
                    Toast.makeText(this, "Satu Item berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()

//                      val result = noteHelper.deleteById(note?.id.toString()).toLong()
//                      if (result > 0){
//                      val intent = Intent()
//                      intent.putExtra(EXTRA_POSITION, position)
//                      setResult(RESULT_DELETE, intent)
//                      finish()
//                    } else {
//                      Toast.makeText(this@NoteAddUpdateActivity, "Gagal menghapus data",Toast.LENGTH_SHORT).show()
//                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}