package com.example.mynotes.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mynotes.R
import com.example.mynotes.databases.NotesDatabases
import com.example.mynotes.databinding.ActivityCreateNoteBinding
import com.example.mynotes.entities.Note
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class CreateNoteActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION: Int = 1
        const val REQUEST_CODE_SELECT_IMAGE: Int = 2000
        const val REQUEST_CODE_PERMISSIONS = 1
        val REQUIRED_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Uri? = result.data?.data
            createNoteBinding.imageNote.setImageURI(data)
            createNoteBinding.imageNote.visibility = View.VISIBLE
            if (data != null) {
                imagePath = data
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectAddImage()
        } else {
            Toast.makeText(this, "No tienes permisos para acceder", Toast.LENGTH_SHORT).show()
        }

    }

    private lateinit var createNoteBinding: ActivityCreateNoteBinding
    private lateinit var selected: String  //default Color
    private  lateinit  var imagePath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNoteBinding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(createNoteBinding.root)

        selected = "#333333"
        onClickImageBack()
        setDataTime()
        setImageSave()
        initMiscellaneous()
        setSubtitleIndicatorColor()

    }

    private fun setImageSave() {
        createNoteBinding.imageSave.setOnClickListener { saveNote() }
    }

    private fun setDataTime() {
        createNoteBinding.textDataTime.text =
            SimpleDateFormat("EEEE , dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(Date())
    }

    private fun onClickImageBack() {
        createNoteBinding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveNote() {
        if (createNoteBinding.inputNoteTitle.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "La nota debe poseer titulo", Toast.LENGTH_SHORT).show()
            return
        } else if (createNoteBinding.inputNoteSubtitle.text.toString().trim().isEmpty()
            && createNoteBinding.inputNote.text.toString().trim().isEmpty()
        ) {
            Toast.makeText(this, "La nota debe contener descripcion", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            title = createNoteBinding.inputNoteTitle.text.toString(),
            subtitle = createNoteBinding.inputNoteSubtitle.text.toString(),
            noteText = createNoteBinding.inputNote.text.toString(),
            dateTime = createNoteBinding.textDataTime.text.toString(),
            color = selected,
            imagePath = imagePath.toString(),
        )

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {

            NotesDatabases.getNotesDatabases(applicationContext)?.noteDao()?.insertNote(note)

            handler.post {

                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    }


    private fun initMiscellaneous() {
        val linearLayoutMiscellaneous: LinearLayout = findViewById(R.id.layoutMiscellaneous)
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(linearLayoutMiscellaneous)
        linearLayoutMiscellaneous.findViewById<View>(R.id.textMiscellaneous).setOnClickListener {

            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }


        val imageColor1: ImageView = linearLayoutMiscellaneous.findViewById(R.id.imageColor)
        val imageColor2: ImageView = linearLayoutMiscellaneous.findViewById(R.id.imageColor2)
        val imageColor3: ImageView = linearLayoutMiscellaneous.findViewById(R.id.imageColor3)
        val imageColor4: ImageView = linearLayoutMiscellaneous.findViewById(R.id.imageColor4)
        val imageColor5: ImageView = linearLayoutMiscellaneous.findViewById(R.id.imageColor5)

        initColor(
            linearLayoutMiscellaneous,
            imageColor1,
            imageColor2,
            imageColor3,
            imageColor4,
            imageColor5
        )

        initAddImage(
            linearLayoutMiscellaneous,
            bottomSheetBehavior,
        )

    }


    private fun initColor(
        linearLayoutMiscellaneous: LinearLayout,
        imageColor1: ImageView,
        imageColor2: ImageView,
        imageColor3: ImageView,
        imageColor4: ImageView,
        imageColor5: ImageView
    ) {
        linearLayoutMiscellaneous.findViewById<View>(R.id.viewColor).setOnClickListener {
            selected = "#000000"
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        linearLayoutMiscellaneous.findViewById<View>(R.id.viewColor2).setOnClickListener {
            selected = "#FDBE3B"
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor1.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        linearLayoutMiscellaneous.findViewById<View>(R.id.viewColor3).setOnClickListener {
            selected = "#FF4842"
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        linearLayoutMiscellaneous.findViewById<View>(R.id.viewColor4).setOnClickListener {
            selected = "#3A52FC"
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        linearLayoutMiscellaneous.findViewById<View>(R.id.viewColor5).setOnClickListener {
            selected = "#116014"
            imageColor5.setImageResource(R.drawable.ic_done)
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            setSubtitleIndicatorColor()
        }
    }

    private fun initAddImage(
        linearLayoutMiscellaneous: LinearLayout,
        bottomSheetBehavior: BottomSheetBehavior<LinearLayout>,
    ) {


        linearLayoutMiscellaneous.findViewById<View>(R.id.layoutAddImage).setOnClickListener {
            selectImage(bottomSheetBehavior)
        }
    }

    private fun selectImage(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    selectAddImage()
                }

                else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            selectAddImage()
        }
    }


    private fun selectAddImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        startForActivityGallery.launch(intent)

    }



    private fun setSubtitleIndicatorColor() {
        val gradientDrawable: GradientDrawable =
            createNoteBinding.viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selected))

    }




}
