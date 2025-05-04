package com.techmania.grocify.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

@Dao
interface AddressDao {

    @Query("SELECT * FROM Address LIMIT 1")
    fun getAddress(): LiveData<Address?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)
}

//@Database(entities = [Address::class], version = 1)
//abstract class AppDatabase : RoomDatabase(){
//    abstract fun addressDao(): AddressDao
//}