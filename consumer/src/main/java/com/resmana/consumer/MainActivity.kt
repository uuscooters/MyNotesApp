package com.resmana.consumer

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.resmana.consumer.adapter.NoteAdapter
import com.resmana.consumer.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import entity.Note
import helper.MappingHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
//    private lateinit var noteHelper: NoteHelper

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Consumer Notes"

        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        fab_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
        }

//  Start      method menggunakan ContentResolver
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                loadNotesAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)
//  End      method menggunakan ContentResolver

//        noteHelper = NoteHelper.getInstance(applicationContext)
//        noteHelper.open()

        if (savedInstanceState == null) {
            // Proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    /*
        Background Thread
        di sini menggunakan fungsi async karena kita menginginkan
        nilai kembalian dari fungsi yg dipanggil.
        untuk mendapatkan nilai kembalian, menggunakan fungsi await()
    */
    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
//              val cursor = noteHelper.queryALL()

                val cursor = contentResolver?.query(
                    CONTENT_URI,
                    null,
                    null,
                    null,
                null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressBar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
             when(requestCode) {
                 // Akan dipanggil jika request codenya ADD
                 NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                     val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)

                     adapter.addItem(note)
                     rv_notes.smoothScrollToPosition(adapter.itemCount - 1)

                     showSnackbarMessage("Satu item berhasil ditambahkan")
                 }

                 // Update dan Delete  memiliki request code sama akan tetapi result codenya berbeda
                 NoteAddUpdateActivity.REQUEST_UPDATE ->
                 when (resultCode) {
                     /*
                     * Akan dipanggil jika result codenya UPDATE
                     * Semua data di load kembali dari awal
                     */

                     NoteAddUpdateActivity.RESULT_UPDATE -> {
                         val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                         val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)

                         adapter.updateItem(position, note)
                         rv_notes.smoothScrollToPosition(position)

                         showSnackbarMessage("Satu item berhasil diubah")
                         }
                        /*
                        * Akan dipanggil jika result codenya DELETE
                        * Delete akan menghapus data dari list berdasarkan dari position
                        * */


                        NoteAddUpdateActivity.RESULT_DELETE -> {
                        val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
                        adapter.removeItem(position)

                        showSnackbarMessage("Satu item berhasil dihapus")
                     }
                 }
             }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
//        noteHelper.close()
    }
}