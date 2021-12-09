package com.devwithluthviar.githubusersapp

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.devwithluthviar.githubusersapp.database.Userdb
import com.devwithluthviar.githubusersapp.repository.UserdbRepository

class MainViewModelUserdb(application: Application) : ViewModel() {
    private val mUserdbRepository: UserdbRepository = UserdbRepository(application)
    fun getAllUserdb(): LiveData<List<Userdb>> = mUserdbRepository.getAllUserdb()
}