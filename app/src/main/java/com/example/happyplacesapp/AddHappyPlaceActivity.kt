package com.example.happyplacesapp

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class AddHappyPlaceActivity : AppCompatActivity() {
    private var binding : ActivityAddHappyPlaceBinding? = null

    private var cameraLauncher : ActivityResultLauncher<Intent> = registerForActivityResult( //replaces startActivityForResult()
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val thumbnail : Bitmap = result.data?.extras!!.get("data") as Bitmap
            binding?.ivLocation?.setImageBitmap(thumbnail)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
    }

    override fun onBackPressed() {
        showAlertDialogOnBack()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
            binding?.ivLocation?.setImageBitmap(thumbnail)
        }
    }


    private fun pickPhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){
                        Toast.makeText(this@AddHappyPlaceActivity, "Storage READ/WRITE Permissions Granted", Toast.LENGTH_SHORT).show()
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
}