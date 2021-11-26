package com.devwithluthviar.githubusersapp

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    var userData = User("", "", "", "", "" , 0, 0, 0)

    override fun createFragment(position: Int): Fragment {
        Log.d("UserDataAdapter", userData.name)
        return HomeFragment.newInstance(position + 1, userData)
    }

    override fun getItemCount(): Int {
        return 2
    }
}
