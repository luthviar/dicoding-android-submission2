package com.devwithluthviar.githubusersapp.helper

import androidx.recyclerview.widget.DiffUtil
import com.devwithluthviar.githubusersapp.database.Userdb

class UserdbDiffCallback(private val mOldUserdbList: List<Userdb>, private val mNewUserdbList: List<Userdb>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOldUserdbList.size
    }
    override fun getNewListSize(): Int {
        return mNewUserdbList.size
    }
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldUserdbList[oldItemPosition].id == mNewUserdbList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldUserdbList[oldItemPosition]
        val newEmployee = mNewUserdbList[newItemPosition]
        return oldEmployee.username == newEmployee.username && oldEmployee.name == newEmployee.name
    }
}