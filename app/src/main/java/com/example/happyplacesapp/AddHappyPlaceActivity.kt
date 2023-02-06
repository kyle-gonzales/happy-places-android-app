package com.example.happyplacesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplacesapp.databinding.ActivityMainBinding

class AddHappyPlaceActivity : AppCompatActivity() {
    private var binding : ActivityAddHappyPlaceBinding? = null
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
    }

    override fun onBackPressed() {
        showAlertDialogOnBack()
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
}