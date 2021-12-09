package com.devwithluthviar.githubusersapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserdbDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(userdb: Userdb)
    @Update
    fun update(userdb: Userdb)
    @Delete
    fun delete(userdb: Userdb)
    @Query("SELECT * from userdb ORDER BY id ASC")
    fun getAllUserdb(): LiveData<List<Userdb>>
}