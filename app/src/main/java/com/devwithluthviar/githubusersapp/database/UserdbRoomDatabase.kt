package com.devwithluthviar.githubusersapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Userdb::class], version = 1)
abstract class UserdbRoomDatabase : RoomDatabase() {

    abstract fun userdbDao(): UserdbDao
    companion object {
        @Volatile
        private var INSTANCE: UserdbRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): UserdbRoomDatabase {
            if (INSTANCE == null) {
                synchronized(UserdbRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserdbRoomDatabase::class.java, "userdb_database")
                        .build()
                }
            }
            return INSTANCE as UserdbRoomDatabase
        }
    }
}