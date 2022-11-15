package com.jellysoft.deliveryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.binding
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.city
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.cityIds
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.username
import com.jellysoft.deliveryapp.R.id
import com.jellysoft.deliveryapp.R.layout
import com.jellysoft.deliveryapp.databinding.ActivityMainBinding
import com.jellysoft.deliveryapp.fragments.*
import com.jellysoft.deliveryapp.util.API
import com.jellysoft.deliveryapp.util.Constant
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var cityList = ArrayList<String>()
    private var cityListIds = ArrayList<String>()
    object GLOBAL {
        @SuppressLint("StaticFieldLeak")
        var binding: ActivityMainBinding? = null
        lateinit var username: String
        lateinit var city: Array<String>
        lateinit var cityIds: Array<String>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = intent.getStringExtra("username").toString()
        binding = DataBindingUtil.setContentView(this, layout.activity_main)

        getStatus()
        binding!!.tabPending.setOnClickListener { loadFragment(PendingFragment()) }
        binding!!.tabComplete.setOnClickListener { loadFragment(CompleteFragment()) }
        binding!!.tabCancelled.setOnClickListener { loadFragment(CancelFragment()) }
        binding!!.tabModified.setOnClickListener { loadFragment(ModFragment()) }
        binding!!.tabRejected.setOnClickListener { loadFragment(RejectFragment()) }
        binding!!.tabProfile.setOnClickListener { loadFragment(ProfileFragment()) }
    }


    private fun getVisibleFragment(): Fragment? {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null && fragment.isVisible) return fragment
        }
        return null
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(id.frame, fragment).commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "onRequestPermissionsResult: ")
    }

    private fun getStatus() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        val jsObj: JsonObject = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "status_master")
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(Constant.API_URL, params, object: AsyncHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result: String = responseBody?.let { String(it) }.toString()
                try {
                    Log.d("city21", result)
                    val mainJson = JSONObject(result)
                    val statusJson = mainJson.getJSONArray(Constant.ARRAY_NAME)
                    for (i in 0 until statusJson.length()) {
                        val status = statusJson.getJSONObject(i)
                        Log.d("city21", status.getString("category_name"))
                        cityList.add(status.getString("category_name"))
                        cityListIds.add(status.getString("id"))
                    }

                    city = Array(cityList.size) { i -> cityList[i] }
                    cityIds = Array(cityList.size) {i -> cityListIds[i]}

                    loadFragment(PendingFragment())

                }catch (e: Exception) {
                    Log.d("err3", e.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {

            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
