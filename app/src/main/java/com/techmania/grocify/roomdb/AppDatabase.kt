package com.techmania.grocify.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Address:: class], version = 1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){
    abstract fun addressDao(): AddressDao

    companion object{

          var INSTANCE: AppDatabase? =null

        fun getDatabase(context: Context): AppDatabase{

            // If the INSTANCE is not null, return it
            // If it is, create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                // Return the newly created instance
                instance
            }
        }
    }
}