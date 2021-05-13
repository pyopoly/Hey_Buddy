package com.comp3617.finalProject

import android.app.Application
import androidx.room.Room
import com.comp3617.assignment2.AppDatabase

/**
 * Create a SQLite Database with Room implementation
 */
class App : Application() {
    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // create database
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java!!, DATABASE_NAME)
            .build()
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: App
        private val DATABASE_NAME = "dbHeyBuddy"

        fun getDB(): App {
            return INSTANCE
        }
    }
}