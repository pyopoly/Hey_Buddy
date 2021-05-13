package com.comp3617.finalProject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.comp3617.finalProject.models.MPlace

@Dao
interface MPlaceDao {

    @Query("SELECT * FROM placesTable WHERE parentBlockId = :blockId")
    fun findByParentId(blockId: Long): MPlace?

    @Insert
    fun insertOne(place: MPlace)

}