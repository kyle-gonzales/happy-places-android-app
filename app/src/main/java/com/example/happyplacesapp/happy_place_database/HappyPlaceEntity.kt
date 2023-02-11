package com.example.happyplacesapp.happy_place_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "happy-places")
data class HappyPlaceEntity(

    @PrimaryKey(autoGenerate =  true)
    val id : Int = 0,
    val name : String,
    val description : String,
    val date : String,
    @ColumnInfo(name = "image-path")
    val image : String,
    val location : String,
    val latitude : Double,
    val longitude : Double,
)
