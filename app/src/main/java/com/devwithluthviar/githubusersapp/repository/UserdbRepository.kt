package com.devwithluthviar.githubusersapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.devwithluthviar.githubusersapp.database.Userdb
import com.devwithluthviar.githubusersapp.database.UserdbDao
import com.devwithluthviar.githubusersapp.database.UserdbRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserdbRepository(application: Application) {
    private val mUserdbDao: UserdbDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = UserdbRoomDatabase.getDatabase(application)
        mUserdbDao = db.userdbDao()
    }
    fun getAllUserdb(): LiveData<List<Userdb>> = mUserdbDao.getAllUserdb()
    fun insert(userdb: Userdb) {
        executorService.execute { mUserdbDao.insert(userdb) }
    }
    fun delete(userdb: Userdb) {
        executorService.execute { mUserdbDao.delete(userdb) }
    }
    fun update(userdb: Userdb) {
        executorService.execute { mUserdbDao.update(userdb) }
    }
}