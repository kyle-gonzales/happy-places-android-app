package com.example.happyplacesapp

import android.app.Application
import com.example.happyplacesapp.happy_place_database.HappyPlaceDatabase

class HappyPlaceApp : Application() {

    val db by lazy {
        HappyPlaceDatabase.getInstance(this)
    }

}