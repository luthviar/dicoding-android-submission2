package com.devwithluthviar.githubusersapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserdbViewModelFactory private constructor(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: UserdbViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): UserdbViewModelFactory {
            if (INSTANCE == null) {
                synchronized(UserdbViewModelFactory::class.java) {
                    INSTANCE = UserdbViewModelFactory(application)
                }
            }
            return INSTANCE as UserdbViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModelUserdb::class.java)) {
            return MainViewModelUserdb(mApplication) as T
        } else if (modelClass.isAssignableFrom(UserdbAddUpdateViewModel::class.java)) {
            return UserdbAddUpdateViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}