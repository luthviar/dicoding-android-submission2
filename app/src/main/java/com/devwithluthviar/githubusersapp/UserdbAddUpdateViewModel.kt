package com.devwithluthviar.githubusersapp

import android.app.Application
import androidx.lifecycle.ViewModel
import com.devwithluthviar.githubusersapp.database.Userdb
import com.devwithluthviar.githubusersapp.repository.UserdbRepository

class UserdbAddUpdateViewModel(application: Application) : ViewModel() {
    private val mUserdbRepository: UserdbRepository = UserdbRepository(application)
    fun insert(userdb: Userdb) {
        mUserdbRepository.insert(userdb)
    }
    fun update(userdb: Userdb) {
        mUserdbRepository.update(userdb)
    }
    fun delete(userdb: Userdb) {
        mUserdbRepository.delete(userdb)
    }
}