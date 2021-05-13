package com.comp3617.assignment2

import androidx.room.Database
import androidx.room.RoomDatabase
import com.comp3617.finalProject.database.*
import com.comp3617.finalProject.models.*

@Database(entities = arrayOf(Group::class, Block::class, MDate::class, MPlace::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun blockDao(): BlockDao
    abstract fun mDateDao(): MDateDao
    abstract fun mPlaceDao(): MPlaceDao
}