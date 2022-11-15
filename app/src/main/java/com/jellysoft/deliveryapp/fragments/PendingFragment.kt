package com.jellysoft.deliveryapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jellysoft.deliveryapp.ItemOrder
import com.jellysoft.deliveryapp.MainActivity
import com.jellysoft.deliveryapp.MainActivity.GLOBAL
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.username
import com.jellysoft.deliveryapp.R.layout
import com.jellysoft.deliveryapp.adapters.PendingOrderAdapter
import com.jellysoft.deliveryapp.databinding.FragmentPendingBinding
import com.jellysoft.deliveryapp.util.API
import com.jellysoft.deliveryapp.util.Constant
import com.jellysoft.deliveryapp.util.Constant.API_URL
import com.jellysoft.deliveryapp.util.NetworkUtils
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class PendingFragment() : Fragment() {
    private lateinit var binding: FragmentPendingBinding
    private var pendingOrderAdapter: PendingOrderAdapter? = null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, layout.fragment_pending, container, false)
        binding.tvname.text = "Hello, $username"
        GLOBAL.binding!!.appHeader.text = "Assigned Order"

        if (context?.let { NetworkUtils.isConnected(it) } == true) {
            getDetails()
        } else {
            Toast.makeText(activity, "Make sure you're connected to Internet", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun getDetails() {
        var client = AsyncHttpClient()
        var params = RequestParams()
        val jsObj: JsonObject = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "pending_orders")
        jsObj.addProperty("username", username)
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(API_URL, params, object: AsyncHttpResponseHandler() {

            override fun onStart() {
                super.onStart()
                binding.pd.visibility = View.VISIBLE
            }

            override fun onFinish() {
                super.onFinish()
                binding.pd.visibility = View.GONE
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result: String = responseBody?.let { String(it) }.toString()
                Log.d("orders", result)
                try{
                    val mainJson = JSONObject(result)
                    val ordersJson = mainJson.getJSONArray(Constant.ARRAY_NAME)
                    val orders = ArrayList<ItemOrder>()

                    for (i in 0 until ordersJson.length()) {
                        val orderJson = ordersJson.getJSONObject(i)

                        val order = ItemOrder()
                        order.date = orderJson.getString("order_date")
                        order.firstname = orderJson.getString("customer_name")
                        order.address = orderJson.getString("customer_address")
                        order.city = orderJson.getString("city_name")
                        order.orderId = orderJson.getString("order_id")

                        orders.add(order)
                    }

                    if(orders.size == 0) {
                        binding.lytNotFound.visibility = View.VISIBLE
                    }else{
                        binding.lytNotFound.visibility = View.GONE
                        pendingOrderAdapter = PendingOrderAdapter(orders, username, "pending")
                        binding.rvOrders.adapter = pendingOrderAdapter
                    }


                } catch (e: Exception){
                    Log.d("err", e.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("orders", "" + responseBody)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}