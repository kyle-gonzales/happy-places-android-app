package com.example.happyplacesapp.happy_place_database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface HappyPlaceDAO {

    @Insert
    suspend fun addHappyPlace(happyPlace : HappyPlaceEntity)

    @Update
    suspend fun updateHappyPlace(happyPlace : HappyPlaceEntity)

    @Delete
    suspend fun deleteHappyPlace(happyPlace: HappyPlaceEntity)

    @Query(value = "SELECT * FROM `happy-places` WHERE id=:id")
    fun getHappyPlaceById(id : Int) : Flow<HappyPlaceEntity>

    @Query(value = "SELECT * FROM `happy-places`")
    fun getAllHappyPlaces() : Flow<List<HappyPlaceEntity>>
}