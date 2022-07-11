package com.example.mynotes.activities
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.adapters.NotesAdapter
import com.example.mynotes.databases.NotesDatabases
import com.example.mynotes.databinding.ActivityMainBinding
import com.example.mynotes.entities.Note
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        const val CREANDO = "CREANDO"
    }

    private lateinit var mainBinding: ActivityMainBinding

    private lateinit var notesAdapter : NotesAdapter

    private  var noteList : ArrayList<Note> = ArrayList<Note>()


    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val texto = result.data?.getStringExtra(CREANDO).orEmpty()
            Toast.makeText(this, texto , Toast.LENGTH_SHORT).show()
            getNotes()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        initRecyclerView()
        setContentView(mainBinding.root)
        onClickImageAddNoteMain()

        getNotes()
    }

    private fun initRecyclerView() {
        mainBinding.notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesAdapter = NotesAdapter(noteList)
        mainBinding.notesRecyclerView.adapter = notesAdapter
        notesAdapter.notifyDataSetChanged()

    }


    private fun onClickImageAddNoteMain() {
        mainBinding.imageAddNoteMain.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            intent.putExtra(CREANDO, "Creando nota")
            resultLauncher.launch(intent)

        }
    }


    private fun getNotes(){
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            /*
                * its like doInBackground()
                * */
            val notes = NotesDatabases.getNotesDatabases(applicationContext)?.noteDao()?.getAllNotes()

            handler.post{
                /*
                * its like onPostExecute()
                * */
                if(noteList.size == 0){
                    noteList.addAll(notes!!)
                    notesAdapter.notifyDataSetChanged()
                }else{
                    noteList.add(0, notes!![0])
                    notesAdapter.notifyItemInserted(0)

                }

                mainBinding.notesRecyclerView.smoothScrollToPosition(0)
                Log.d("Mis notas", notes.toString())
            }
        }
    }


}