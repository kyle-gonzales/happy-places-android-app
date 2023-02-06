package com.example.happyplacesapp

import android.app.DatePickerDialog
import android.icu.util.Calendar
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

        binding?.etDate?.setOnClickListener {
            showCalendarDialog()
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

    private fun showCalendarDialog() {

        //getting current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        //choosing custom date
        val dpd = DatePickerDialog(this,
            {_, year, month, day ->
                val dateText = "${month + 1}/$day/$year"
                binding?.etDate?.setText(dateText)
            }
        , year, month, day)

        dpd.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dpd.show()
    }


}