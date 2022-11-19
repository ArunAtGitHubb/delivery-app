package com.jellysoft.deliveryapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jellysoft.deliveryapp.LoginActivity
import com.jellysoft.deliveryapp.MainActivity
import com.jellysoft.deliveryapp.MainActivity.GLOBAL
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.username
import com.jellysoft.deliveryapp.R
import com.jellysoft.deliveryapp.R.layout
import com.jellysoft.deliveryapp.databinding.FragmentProfileBinding
import com.jellysoft.deliveryapp.db.DeliveryDbHelper
import com.jellysoft.deliveryapp.util.API
import com.jellysoft.deliveryapp.util.Constant
import com.jellysoft.deliveryapp.util.Constant.API_URL
import com.jellysoft.deliveryapp.util.DeliveryReaderContract.DeliveryEntry.COLUMN_NAME_USER_NAME
import com.jellysoft.deliveryapp.util.DeliveryReaderContract.DeliveryEntry.TABLE_DELIVERY
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, layout.fragment_profile, container, false)
        GLOBAL.binding!!.appHeader.text = "Profile"
        getDetails()

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure want to logout")
        builder.setTitle("Confirm?").setCancelable(false)
        builder.setPositiveButton("Yes") { _, _ ->
            logoutUser()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        binding.lytLogOut.setOnClickListener {
            builder.create().show()
        }
        binding.imgUser
        return binding.root
    }

    private fun getDetails() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        val jsObj = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "login_profile")
        jsObj.addProperty("username", username)
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(API_URL, params, object: AsyncHttpResponseHandler() {

            override fun onStart() {
                super.onStart()
                binding.infoLinear.visibility = View.GONE
                binding.pd.visibility = View.VISIBLE
            }

            override fun onFinish() {
                super.onFinish()
                binding.infoLinear.visibility = View.VISIBLE
                binding.pd.visibility = View.GONE
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }

                try{
                    val mainJson = result?.let { JSONObject(it) }
                    val userJson = mainJson?.getJSONArray(Constant.ARRAY_NAME)

                    val user = userJson?.getJSONObject(0)
                    Log.d("user21", user.toString())
                    binding.username.text = user?.getString("category_name")
                    binding.deliveryCount.text = user?.getString("total_deliveries")
                    binding.mobile.text = user?.getString("phone_number")
                    binding.thisMonthTotal.text = user?.getString("this_month_total")
                    binding.thisMonthCompleted.text = user?.getString("this_month_completed")
                    binding.thisMonthMod.text = user?.getString("this_month_modified")

                    Glide.with(binding.root.context)
                        .load(user?.getString("category_image"))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(binding.imgUser)

                } catch (e: Exception) {

                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {}
        })
    }

    fun logoutUser() {
        MainActivity().finish()
        val dbHelper = context?.let { DeliveryDbHelper(it.applicationContext) }
        val db = dbHelper?.writableDatabase
        db?.delete(TABLE_DELIVERY, "$COLUMN_NAME_USER_NAME = ?", arrayOf(username))
        startActivity(Intent(context, LoginActivity::class.java))
    }
}