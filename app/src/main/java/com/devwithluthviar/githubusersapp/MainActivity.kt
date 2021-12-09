package com.devwithluthviar.githubusersapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    companion object {
        const val TOKEN_AUTH_GITHUB = "token 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"
    }
    private lateinit var rvHeroes: RecyclerView
    var usersResponse = ArrayList<User>()
    var usersResult = ArrayList<User>()
    val loading = LoadingDialog(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rvHeroes = findViewById(R.id.rv_heroes)
        rvHeroes.setHasFixedSize(true)
        loading.startLoading()
        showUsers()
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({

            val pref = SettingPreferences.getInstance(dataStore)
            val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
                MainViewModel::class.java
            )

            mainViewModel.getThemeSettings().observe(this,
                { isDarkModeActive: Boolean ->
                    if (isDarkModeActive) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                })
        }, 1000)
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
            Toast.makeText(this@MainActivity, "No Data", Toast.LENGTH_SHORT).show()
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
        val detailUser = Intent(this@MainActivity, DetailUserActivity::class.java)
        detailUser.putExtra(DetailUserActivity.USER_DATA, user)
        detailUser.putExtra(DetailUserActivity.IS_FROM_FAVORITE, false)
        startActivity(detailUser)
    }

    private fun showUsersWhenSearch(text: String) {
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=${text}"
        client.setUserAgent("request")
        client.addHeader("Authorization", TOKEN_AUTH_GITHUB)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                val result = String(responseBody)
                try {
                    val jsonObject = JSONObject(result)
                    val items = jsonObject.getString("items")
                    val jsonArray = JSONArray(items)
                    Log.d("jsonarraylengthcheck", "${jsonArray.length()}")
                    if (jsonArray.length() < 1) {
                        showRecyclerList(ArrayList())
                    } else {
                        for (jsonIndex in 0..(jsonArray.length() - 1)) {
                            val username = jsonArray.getJSONObject(jsonIndex).getString("login")
                            getDetailUser(username, true)
                        }
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                usersResult = ArrayList<User>()
                if(query != "") {
                    loading.startLoading()
                    Log.d("onQueryTextSubmitIf", query)
                    showUsersWhenSearch(query)
                } else {
                    Log.d("onQueryTextSubmitElse", query)
                    showRecyclerList(usersResponse)
                }
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                Log.e("onQueryTextChange", "onQueryTextChange: ${newText}")
                if(newText == "") {
                    showRecyclerList(usersResponse)
                }
                return false
            }
        })
        return true
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
                    if(isSearch == true) {
                        usersResult.add(lastUser)
                        showRecyclerList(usersResult)
                    } else {
                        usersResponse.add(lastUser)
                        showRecyclerList(usersResponse)
                    }

                    loading.isDismiss()


                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.menu_favorite -> {
                Toast.makeText(this@MainActivity, "This is favorite page", Toast.LENGTH_SHORT).show()
                val favoriteActivity = Intent(this@MainActivity, FavoriteActivity::class.java)
                //detailUser.putExtra(DetailUserActivity.USER_DATA, user)
                startActivity(favoriteActivity)
                return true
            }
            R.id.menu_theme -> {

                val pref = SettingPreferences.getInstance(dataStore)
                val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
                    MainViewModel::class.java
                )
                mainViewModel.getThemeSettings().observe(this,
                    { isDarkModeActive: Boolean ->
                        Log.d("valuedark", "${isDarkModeActive}")
                        if (isDarkModeActive) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            Toast.makeText(this@MainActivity, "Dark mode enable", Toast.LENGTH_SHORT).show()
                            mainViewModel.saveThemeSetting(false)
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            Toast.makeText(this@MainActivity, "Dark mode disabled", Toast.LENGTH_SHORT).show()
                            mainViewModel.saveThemeSetting(true)
                        }
                    })
                return true
            }
        }
        Log.d("onOptionsItemSelected", "onOptionsItemSelected ${item.toString()}")
        return true
    }
}