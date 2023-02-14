package com.example.happyplacesapp.activities

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import android.Manifest
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.example.happyplacesapp.happy_place_database.HappyPlaceDAO
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity
import com.example.happyplacesapp.utils.Constants
import com.example.happyplacesapp.utils.HappyPlaceApp
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class AddHappyPlaceActivity : AppCompatActivity() {
    private var binding : ActivityAddHappyPlaceBinding? = null
    private var thumbnailUri : Uri? = null
    private var thumbnailPath : String? = null
    private var longitude : Double = 0.0
    private var latitude : Double = 0.1


    private var cameraLauncher : ActivityResultLauncher<Intent> = registerForActivityResult( //replaces startActivityForResult()
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val thumbnail : Bitmap = result.data?.extras!!.get("data") as Bitmap
            thumbnailUri = saveImage(thumbnail)
            thumbnailPath = getFilePath(thumbnailUri!!)
            Log.e("saved file", thumbnailPath!!)
            binding?.ivLocation?.setImageBitmap(thumbnail)
        }
    }

    private var galleryLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val uri = result.data?.data
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                thumbnailUri = saveImage(bitmap)
                thumbnailPath = getFilePath(thumbnailUri!!)
                Log.e("saved file", thumbnailPath!!)
                binding?.ivLocation?.setImageBitmap(bitmap)

            } catch (e: Exception) {
                Toast.makeText(this@AddHappyPlaceActivity, "Image Size Too Large", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    //updating an existing happy place
    private var happyPlace : HappyPlaceEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val happyPlaceDao = (application as HappyPlaceApp).db.getHappyPlaceDao()

        if (intent.hasExtra(Constants.RV_HAPPY_PLACE_ITEM)) {
            happyPlace = intent.getSerializableExtra(Constants.RV_HAPPY_PLACE_ITEM) as HappyPlaceEntity
        }

        if (happyPlace != null) {
            supportActionBar?.title = "Edit Happy Place"

            binding?.btnSave?.text = "Update"

            binding?.etTitle?.setText(happyPlace?.name)
            binding?.etDescription?.setText(happyPlace?.description)
            binding?.etDate?.setText(happyPlace?.date)
            binding?.etLocation?.setText(happyPlace?.location)
            binding?.ivLocation?.setImageURI(Uri.parse(happyPlace?.image))

            try{
                thumbnailPath = Uri.parse(happyPlace?.image).toString()
//                Toast.makeText(this, thumbnailPath, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "thumbnail error", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        binding?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.etDate?.setOnClickListener {
            showCalendarDialog()
        }
        binding?.tvAddImage?.setOnClickListener {
            showAlertDialogOnImageSelect()
        }

        binding?.btnSave?.setOnClickListener {
            addHappyPlace(happyPlaceDao)
        }
    }

    override fun onBackPressed() {
        showAlertDialogOnBack()
    }

    private fun getFilePath(uri: Uri) : String? {

        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        return contentResolver.query(uri, projection, null, null, null)?.use {
            cursor -> if (!cursor.moveToFirst()) return@use null
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

//            val name = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) // gets the name, not the path
            cursor.getString(columnIndex)
        }
    }
    private fun getFileName(uri: Uri) : String {
        val docFile = DocumentFile.fromSingleUri(this, uri)
        return docFile?.name.toString()
    }
    private fun addHappyPlace(happyPlaceDao: HappyPlaceDAO) {

        if (!isValidHappyPlace(binding?.etTitle?.text.toString(), binding?.etDescription?.text.toString(), thumbnailPath!!, binding?.etDate?.text.toString(), binding?.etLocation?.text.toString())) {

            Toast.makeText(this, "Invalid input. Make sure to fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val name = binding?.etTitle?.text.toString()
        val description = binding?.etDescription?.text.toString()
        val date = binding?.etDate?.text.toString()
        val location = binding?.etLocation?.text.toString()

        lifecycleScope.launch {

            if (happyPlace != null) { // does not update
                happyPlaceDao.updateHappyPlace(HappyPlaceEntity(id = happyPlace!!.id, name = name, description = description, image = thumbnailPath!!, date = date, location = location, latitude = latitude, longitude = longitude))
                Toast.makeText(this@AddHappyPlaceActivity, "happy place updated", Toast.LENGTH_SHORT).show()
                finish()

            } else {
                happyPlaceDao.addHappyPlace(HappyPlaceEntity(name = name, description = description, image = thumbnailPath!!, date = date, location = location, latitude = latitude, longitude = longitude))
                Toast.makeText(this@AddHappyPlaceActivity, "happy place saved", Toast.LENGTH_SHORT).show()
                finish()
            }

            binding?.etTitle?.text?.clear()
            binding?.etDate?.text?.clear()
            binding?.etDescription?.text?.clear()
            binding?.etLocation?.text?.clear()
            thumbnailUri = null
            thumbnailPath = null
            longitude = 0.0
            latitude = 0.0
        }
    }



    private fun isValidHappyPlace(name : String, description: String, image : String, date : String, location : String, latitude : Double = 0.0, longitude : Double = 0.0): Boolean {
        return (name.isNotEmpty() &&
                description.isNotEmpty() &&
                image.isNotEmpty() &&
                date.isNotEmpty() &&
                location.isNotEmpty() &&
                ! latitude.isNaN() &&
                ! longitude.isNaN())
    }

    private fun showAlertDialogOnImageSelect() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Select Action")
            .setCancelable(false)
            /*
            * alternatively, you can set a list of items to choose from:
            * val dialogItems = arrayOf("Select photo from gallery", "capture photo from camera")
            * builder.setItems(dialogItems) {dialog, item -> when(item){...}}
            * */
            .setPositiveButton("Select Photo From Gallery") { _, _ ->
                pickPhotoFromGallery()
            }
            .setNegativeButton("Capture Photo From Camera") {_, _ ->
//                Toast.makeText(this, "opening camera", Toast.LENGTH_SHORT).show()
                takePhotoFromCamera()
            }

        builder.create().show()
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(object : PermissionListener {
            override fun onPermissionGranted(report: PermissionGrantedResponse?) {

                val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //starts camera
                cameraLauncher.launch(camIntent)
            }
            override fun onPermissionDenied(report: PermissionDeniedResponse?) {
                Toast.makeText(this@AddHappyPlaceActivity, "denied", Toast.LENGTH_SHORT).show()
                showRationalDialogForPermissions()
            }
            override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }
        }).onSameThread().check()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == 1) {
//            val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
//            binding?.ivLocation?.setImageBitmap(thumbnail)
//        }
//    }

    private fun pickPhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){
//                        Toast.makeText(this@AddHappyPlaceActivity, "Storage READ/WRITE Permissions Granted", Toast.LENGTH_SHORT).show()

                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryLauncher.launch(galleryIntent)
                    }
                    else if(report.isAnyPermissionPermanentlyDenied){
                        // after denying permissions twice, the app is permanently denied. That is when you want
//                        Toast.makeText(this@AddHappyPlaceActivity, "permanently denied", Toast.LENGTH_SHORT).show()
                        showRationalDialogForPermissions()
                    }else{
                        Toast.makeText(this@AddHappyPlaceActivity, "we need access", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown( // asks the user for permissions
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
         val builder = AlertDialog.Builder(this)
         builder.setMessage("We need access this permission for this feature to function correctly. Enable permissions in the Application Settings")
             .setCancelable(true)
             .setPositiveButton("Settings") {_, _ ->
                 try{
                     val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS) // does not work
                     val uri = Uri.fromParts("package", packageName, null)
                     settingsIntent.data = uri
                     startActivity(settingsIntent)
                     // startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {data = Uri.fromParts("package", packageName, null)}

                 } catch (e: ActivityNotFoundException) {
                     Toast.makeText(this@AddHappyPlaceActivity,"cannot open settings", Toast.LENGTH_SHORT).show()
                     e.printStackTrace()
                 }
             }
             .setNegativeButton("Cancel") {dialog, _ ->
                 dialog.dismiss()
             }
        builder.create().show()
    }

    private fun saveImageOld(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        val dir = wrapper.getDir("HappyPlacesImages", Context.MODE_PRIVATE)

        val file = File(dir, "Place${System.currentTimeMillis() / 1000}.jpg")

        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            Toast.makeText(this@AddHappyPlaceActivity,"failed to save image", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        return Uri.parse(dir.absolutePath)

    }

    private fun saveImage(bitmap: Bitmap): Uri? {
        if (Build.VERSION.SDK_INT >= 29) {
            val name = "Place${System.currentTimeMillis() / 1000}.jpg"
            val relativeLocation = Environment.DIRECTORY_DCIM + "/Happy Places"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation)
            }
                val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                val stream : OutputStream? = contentResolver.openOutputStream(uri!!)
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                contentValues.put(MediaStore.Images.Media.IS_PENDING,false)
                contentResolver.update(uri, contentValues, null, null)
                return uri
            } catch (e: java.lang.Exception){
                e.printStackTrace()
            } finally {
                stream?.flush()
                stream?.close()
            }
        } else{
            // add support for older android
            return null
        }
        return null
    }

    private fun showAlertDialogOnBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discard New Happy Place?")
            .setMessage("Your changes will not be saved")
            .setPositiveButton("Discard") {
                dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun showCalendarDialog() {

        //getting current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //choosing custom date
        val dpd = DatePickerDialog(this,
            {_, year_, month_, day_ ->
                val dateText = "${month_ + 1}/$day_/$year_"
                binding?.etDate?.setText(dateText)
            }
        , year, month, day)

        dpd.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dpd.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}