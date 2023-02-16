package com.example.happyplacesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.R
import com.example.happyplacesapp.databinding.ActivityMapBinding
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity
import com.example.happyplacesapp.utils.Constants

class MapActivity : AppCompatActivity() {

    private var binding : ActivityMapBinding? = null
    private var happyPlaceItem : HappyPlaceEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbar?.setNavigationOnClickListener {
            finish()
        }
        if (intent.hasExtra(Constants.RV_HAPPY_PLACE_ITEM)) {
            happyPlaceItem= intent.getSerializableExtra(Constants.RV_HAPPY_PLACE_ITEM) as HappyPlaceEntity
            supportActionBar?.title = happyPlaceItem?.name
        } else {
            supportActionBar?.title = "Your Happy Place"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}