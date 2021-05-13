package com.comp3617.finalProject.database

import androidx.room.*
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.MDate

@Dao
interface MDateDao {
    @get:Query("SELECT * FROM mDatesTable")
    val allMDates: List<MDate>

    @Query("SELECT * FROM mDatesTable WHERE parentGroupId LIKE :id")
    fun findChildrenByParentMDateId(id: Long): List<MDate>

    @Query("SELECT dateId FROM mDatesTable WHERE parentGroupId = :id LIMIT 1")
    fun findOneByParentGroupId(id: Long): Long?

    @Query("SELECT * FROM mDatesTable WHERE dateId = :id")
    fun findById(id: Long): MDate?

    @Insert
    fun insertAll(dates: List<MDate>): List<Long>

    @Insert
    fun insertOne(date: MDate)

    @Update
    fun update(date: MDate)

    @Delete
    fun delete(date: MDate)
}