package com.devwithluthviar.githubusersapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    companion object {
        const val USER_DATA = "user_data"
    }

    private val TAB_TITLES = intArrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        val tvObject: TextView = findViewById(R.id.tv_object_received)
        val imageObject: ImageView = findViewById(R.id.image_detail_user)

        val userData = intent.getParcelableExtra<User>(USER_DATA) as User

        val text =
            "Username : ${userData.username},\n" +
                    "Name : ${userData.name},\n" +
                    "Avatar : ${userData.avatar},\n" +
                    "Company : ${userData.company},\n" +
                    "Location : ${userData.location},\n" +
                    "Repository : ${userData.repository.toString()},\n" +
                    "Follower : ${userData.follower.toString()},\n" +
                    "Following : ${userData.following.toString()}"

        tvObject.text = text

//        for local image
//        val id : Int = getResources().getIdentifier("com.devwithluthviar.githubusersapp:"+userData.avatar, null, null);
//        imageObject.setImageResource(id)

//        for url image
        Glide.with(this)
            .load(userData.avatar)
            .into(imageObject)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.userData = userData
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}