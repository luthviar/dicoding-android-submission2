package com.devwithluthviar.githubusersapp

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class HomeFragment : Fragment() {


    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(index: Int, userData: User) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                    putParcelable(DetailUserActivity.USER_DATA, userData)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvLabel: TextView = view.findViewById(R.id.section_label)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)!!
        val userData = arguments?.getParcelable<User>(DetailUserActivity.USER_DATA)!! as User
        Log.d("checkparcelable", "${userData.name} & ${index}")

        tvLabel.textSize = 18F

        tvLabel.text = "Loading..."


        //FOLLOWING & FOLLOWERS API
        val client = AsyncHttpClient()
        var url = "https://api.github.com/users/${userData.username}/following"
        if(index > 1) {
            url = "https://api.github.com/users/${userData.username}/followers"
        }
        client.setUserAgent("request")
        client.addHeader("Authorization", MainActivity.TOKEN_AUTH_GITHUB)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                val result = String(responseBody)
                try {

                    val datas = JSONArray(result)
                    var theListString = ""
                    for (jsonIndex in 0..(datas.length() -1)) {
                        val currentFollowerUsername = datas.getJSONObject(jsonIndex).getString("login")
                        if(theListString == "") {
                            theListString = "<br/><br/><a href='https://github.com/${currentFollowerUsername}'>" + currentFollowerUsername + "</a>"
                        } else {
                            theListString = theListString + "<br/> " + "<a href='https://github.com/${currentFollowerUsername}'>" + currentFollowerUsername + "</a>"
                        }
                    }
                    if(datas.length() < 1) {
                        tvLabel.text = "Tidak ada data..."
                    } else {
                        Log.d("theFollowerListString", theListString)
                        tvLabel.linksClickable = true
                        tvLabel.isClickable = true
                        tvLabel.movementMethod = LinkMovementMethod.getInstance()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            tvLabel.text = Html.fromHtml(theListString, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            tvLabel.text = Html.fromHtml(theListString)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("catcherror", e.message.toString())
                    e.printStackTrace()
                    tvLabel.text = "Terdapat kesalahan saat mengambil data..."
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
                tvLabel.text = "Terdapat kesalahan koneksi..."
            }
        })

    }
}