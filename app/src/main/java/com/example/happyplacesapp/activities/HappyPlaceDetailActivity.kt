package com.example.happyplacesapp.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.Constants
import com.example.happyplacesapp.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity

class HappyPlaceDetailActivity : AppCompatActivity() {

    private var binding : ActivityHappyPlaceDetailBinding? = null

    private var happyPlaceItem : HappyPlaceEntity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        happyPlaceItem = intent.getSerializableExtra(Constants.RV_HAPPY_PLACE_ITEM) as HappyPlaceEntity // consider using a Parcelable instead of a Serializable
        // https://stackoverflow.com/questions/3323074/android-difference-between-parcelable-and-serializable

        if (happyPlaceItem != null) {
            setSupportActionBar(binding?.toolbar)
            if (supportActionBar != null) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            binding?.toolbar?.title = happyPlaceItem?.name.toString()
            binding?.toolbar?.setNavigationOnClickListener {
                finish()
            }
            binding?.ivLocation?.setImageURI(Uri.parse(happyPlaceItem?.image))
            binding?.tvDescription?.text = happyPlaceItem?.description
            binding?.tvLocation?.text = happyPlaceItem?.location
        }
    }
}