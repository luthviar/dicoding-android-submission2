package com.devwithluthviar.githubusersapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.devwithluthviar.githubusersapp.database.Userdb
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*
import kotlin.concurrent.schedule



class DetailUserActivity : AppCompatActivity() {

    companion object {
        const val USER_DATA = "user_data"
        const val IS_FROM_FAVORITE = "is_from_favorite"
        const val USERDB = "userdb"
    }

    private val TAB_TITLES = intArrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
    )

    val loading = LoadingDialog(this)
    private var isEdit = false
    private var justRemoved = false

    private var userdb: Userdb? = null
    private lateinit var userdbAddUpdateViewModel: UserdbAddUpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        loading.startLoading()

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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        Handler().postDelayed({
//            loading.isDismiss()
//        }, 1500)

        Timer().schedule(1500) {
            loading.isDismiss()
        }

        val mainViewModel = obtainViewModelList(this@DetailUserActivity)
        mainViewModel.getAllUserdb().observe(this, { userdbList ->
            if (userdbList != null) {
                for ((index, value) in userdbList.withIndex()) {
                    Log.d("apaniindex", "${value.username}")
                    if(value.username == userData.username) {
                        userdb = userdbList[index]
                        break
                    }
                }


                userdbAddUpdateViewModel = obtainViewModel(this@DetailUserActivity)

                if (userdb != null) {
                    Log.d("nonulkah", "comhere")
                    isEdit = true
                } else {
                    userdb = Userdb()
                }

                val fab: View = findViewById(R.id.fav_icon)
                val fabRemove: View = findViewById(R.id.fav_icon_remove)

                if (isEdit == true) {
                    if(justRemoved == false) {
                        fab.visibility = View.GONE
                        fabRemove.visibility = View.VISIBLE
                    }
                    Log.d("comehereisedittrue", "${userdb?.username}")
                    Toast.makeText(this@DetailUserActivity, "Username: ${userdb!!.username}", Toast.LENGTH_SHORT).show()
                } else {
                    fabRemove.visibility = View.GONE
                    fab.visibility = View.VISIBLE

                    Toast.makeText(this@DetailUserActivity, "No Data", Toast.LENGTH_SHORT).show()
                }

                fab.setOnClickListener { view ->
                    userdb.let { userdb ->
                        userdb?.username = userData.username
                        userdb?.name = userData.name
                        userdb?.avatar = userData.avatar
                        userdb?.company = userData.company
                        userdb?.location = userData.location
                        userdb?.repository = userData.repository
                        userdb?.follower = userData.follower
                        userdb?.following = userData.following
                    }
                    userdbAddUpdateViewModel.insert(userdb as Userdb)
                    justRemoved = false
                    fab.visibility = View.GONE
                    fabRemove.visibility = View.VISIBLE
                    Snackbar.make(view, "Berhasil menambahkan menjadi favorit", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }

                fabRemove.setOnClickListener { view ->
                    if(isEdit == true) {
                        userdbAddUpdateViewModel.delete(userdb as Userdb)
                        justRemoved = true
                    }
                    fabRemove.visibility = View.GONE
                    fab.visibility = View.VISIBLE
                    Snackbar.make(view, "Berhasil menghapus favorit", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }






                //userdb = intent.getParcelableExtra(USERDB)





                //end inside
            }
        })



    }

    private fun obtainViewModel(activity: AppCompatActivity): UserdbAddUpdateViewModel {
        val factory = UserdbViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserdbAddUpdateViewModel::class.java)
    }

    private fun obtainViewModelList(activity: AppCompatActivity): MainViewModelUserdb {
        val factory = UserdbViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModelUserdb::class.java)
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}