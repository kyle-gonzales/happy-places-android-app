package com.example.happyplacesapp.activities

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.R
import com.example.happyplacesapp.databinding.ActivityMapBinding
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity
import com.example.happyplacesapp.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onMapReady(map: GoogleMap?) {
        val location = LatLng(happyPlaceItem!!.latitude, happyPlaceItem!!.longitude)
        map?.addMarker(MarkerOptions().position(location).title(happyPlaceItem!!.name))

        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(location, 15.0f)

//        map?.moveCamera(CameraUpdateFactory.newLatLng(location))
//        map?.moveCamera(newLatLngZoom)

        map?.animateCamera(newLatLngZoom)
    }
}