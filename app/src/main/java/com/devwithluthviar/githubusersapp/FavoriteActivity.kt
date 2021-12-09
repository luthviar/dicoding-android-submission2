package com.devwithluthviar.githubusersapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FavoriteActivity : AppCompatActivity() {

    companion object {
        const val TOKEN_AUTH_GITHUB = "token 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"
    }
    private lateinit var rvHeroes: RecyclerView
    var usersResponse = ArrayList<User>()
    val loading = LoadingDialog(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        rvHeroes = findViewById(R.id.rv_heroes)
        rvHeroes.setHasFixedSize(true)

        supportActionBar?.elevation = 0f

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Favorite User"

        loading.startLoading()

        val mainViewModel = obtainViewModel(this@FavoriteActivity)
        mainViewModel.getAllUserdb().observe(this, { userdbList ->
            if (userdbList != null) {
                Log.d("checkcontent", "apaniw: ${userdbList.get(0).username}")
                Toast.makeText(this@FavoriteActivity, "segini: ${userdbList.count()}", Toast.LENGTH_SHORT).show()
                usersResponse.clear()
                for ((index, value) in userdbList.withIndex()) {
                    val username = value.username
                    val name = value.name
                    val avatar = value.avatar
                    val company = value.company
                    val location = value.location
                    val repository = value.repository
                    val follower = value.follower
                    val following = value.following

                    val lastUser = User(
                        username = username!!,
                        name = name!!,
                        avatar = avatar!!,
                        company = company!!,
                        location = location!!,
                        repository = repository!!,
                        follower = follower!!,
                        following = following!!
                    )
                    usersResponse.add(lastUser)
                }
                loading.isDismiss()
                showRecyclerList(usersResponse)
                //showUsers()
            }
        })




    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModelUserdb {
        val factory = UserdbViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModelUserdb::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showRecyclerList(listUserData: ArrayList<User> = ArrayList()) {
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvHeroes.layoutManager = GridLayoutManager(this, 2)
        } else {
            rvHeroes.layoutManager = LinearLayoutManager(this)
        }
        var listUserAdapter = ListUserAdapter(ArrayList(), this)
        if(listUserData.count() < 1) {
            rvHeroes.visibility = View.GONE
            Toast.makeText(this@FavoriteActivity, "No Data", Toast.LENGTH_SHORT).show()
        } else {
            rvHeroes.visibility = View.VISIBLE
            listUserAdapter = ListUserAdapter(listUserData, this)
            rvHeroes.adapter = listUserAdapter
        }
        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedHero(data)
            }
        })
    }

    private fun showSelectedHero(user: User) {
        val detailUser = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
        detailUser.putExtra(DetailUserActivity.USER_DATA, user)
        detailUser.putExtra(DetailUserActivity.IS_FROM_FAVORITE, true)
        startActivity(detailUser)
    }

    private fun getDetailUser(username: String, isSearch: Boolean = false) {
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/${username}"
        client.setUserAgent("request")
        client.addHeader("Authorization", TOKEN_AUTH_GITHUB)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                val result = String(responseBody)
                Log.d("getDetailUserOnSuccess", result)
                try {
                    val responseObject = JSONObject(result)

                    val username = responseObject.getString("login")
                    val name = responseObject.getString("name")
                    val avatar = responseObject.getString("avatar_url")
                    val company = responseObject.getString("company")
                    val location = responseObject.getString("location")
                    val repository = responseObject.getString("public_repos")
                    val follower = responseObject.getString("followers")
                    val following = responseObject.getString("following")

                    val lastUser = User(
                        username = username,
                        name = name,
                        avatar = avatar,
                        company = company,
                        location = location,
                        repository = repository.toInt(),
                        follower = follower.toInt(),
                        following = following.toInt()
                    )

                    usersResponse.add(lastUser)
                    showRecyclerList(usersResponse)


                    loading.isDismiss()
                } catch (e: Exception) {
                    Toast.makeText(this@FavoriteActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.d("TAGTAG", errorMessage)
                Toast.makeText(this@FavoriteActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showUsers() {
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users"
        client.setUserAgent("request")
        client.addHeader("Authorization", TOKEN_AUTH_GITHUB)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                val result = String(responseBody)
                try {
                    val jsonArray = JSONArray(result)
                    for (jsonIndex in 0..(jsonArray.length() - 1)) {
                        val username = jsonArray.getJSONObject(jsonIndex).getString("login")
                        getDetailUser(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@FavoriteActivity, e.message, Toast.LENGTH_SHORT).show()
                    Log.d("catcherror", e.message.toString())
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                Log.e("ONAPI", "onfailuree: ${responseBody}")
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.e("ONAPI", errorMessage)
                Toast.makeText(this@FavoriteActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}