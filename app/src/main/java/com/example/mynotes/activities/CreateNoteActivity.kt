package com.example.mynotes.activities

import android.Manifest
import android.R.attr.data
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import com.example.mynotes.R
import com.example.mynotes.databases.NotesDatabases
import com.example.mynotes.databinding.ActivityCreateNoteBinding
import com.example.mynotes.entities.Note
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class CreateNoteActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION: Int = 1
        const val REQUEST_CODE_SELECT_IMAGE: Int = 2000
        const val REQUEST_CODE_PERMISSIONS = 1
         val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE  )

    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val photoUri: Uri? = result.data?.data

            // Load the image located at photoUri into selectedImage

            // Load the image located at photoUri into selectedImage
            val selectedImage = loadFromUri(photoUri)

            createNoteBinding.imageNote.setImageBitmap(selectedImage)
            createNoteBinding.imageNote.visibility = View.VISIBLE


        }
    }

    private lateinit var createNoteBinding: ActivityCreateNoteBinding
    private lateinit var selected: String  //default Color

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
            return;
        } else if (createNoteBinding.inputNoteSubtitle.text.toString().trim().isEmpty()
            && createNoteBinding.inputNote.text.toString().trim().toString().isEmpty()
        ) {
            Toast.makeText(this, "La nota debe contener descripcion", Toast.LENGTH_SHORT).show()
            return;
        }

        val note = Note(
            title = createNoteBinding.inputNoteTitle.text.toString(),
            subtitle = createNoteBinding.inputNoteSubtitle.text.toString(),
            noteText = createNoteBinding.inputNote.text.toString(),
            dateTime = createNoteBinding.textDataTime.text.toString(),
            color = selected
        )

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            /*
                * its like doInBackground()
                * */
            NotesDatabases.getNotesDatabases(applicationContext)?.noteDao()?.insertNote(note)

            handler.post {
                /*
                * its like onPostExecute()
                * */

                val intent: Intent = Intent()
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                selectAddImage()
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            selectAddImage()
        }


    }


    private fun selectAddImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )




        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
            // Bring up gallery to select a photo
            resultLauncher.launch(intent)


    }

    private fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(this.contentResolver, photoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }





    private fun setSubtitleIndicatorColor() {
        val gradientDrawable: GradientDrawable =
            createNoteBinding.viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selected))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.size > 0 && requestCode == REQUEST_CODE_PERMISSIONS) {
                if(grantResults[1] == PackageManager.PERMISSION_GRANTED)
                selectAddImage()
            } else {
                Toast.makeText(this,
                    "Se requieren permisos para utilizar la galeria",
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
}

