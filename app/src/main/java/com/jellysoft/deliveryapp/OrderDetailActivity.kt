package com.jellysoft.deliveryapp

import android.R.layout
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jellysoft.deliveryapp.MainActivity.GLOBAL
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.binding
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.city
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.cityIds
import com.jellysoft.deliveryapp.MainActivity.GLOBAL.username
import com.jellysoft.deliveryapp.adapters.OrderItemAdapter
import com.jellysoft.deliveryapp.databinding.ActivityOrderDetailBinding
import com.jellysoft.deliveryapp.util.API
import com.jellysoft.deliveryapp.util.Constant
import com.jellysoft.deliveryapp.util.Constant.API_URL
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class OrderDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderDetailBinding
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var item: OrderItemAdapter
    private lateinit var itemOrder: ItemOrder

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        bottomSheetDialog = BottomSheetDialog(this)
        val orderId = intent.getStringExtra("orderId")
        val username = intent.getStringExtra("username")
        val from = intent.getStringExtra("from")
        Log.d("order", orderId + username + from)

        getDetails(orderId)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm?").setCancelable(false)
        builder.setPositiveButton("Yes") { _, _ ->
            updateStatus(cityIds[binding.spinner.selectedItemPosition], orderId)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        if(from == "pending") {
            binding.actionBtn.setOnClickListener {
                builder.setMessage("Are you sure change this order as " + city[binding.spinner.selectedItemPosition])
                builder.create().show()
            }
        } else {
            binding.actionBtn.visibility = View.GONE
            binding.spinner.visibility = View.GONE
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, layout.simple_spinner_item, city)
        adapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
    }

    private fun updateStatus(statusId: String, orderId: String?) {
        var client = AsyncHttpClient()
        var params = RequestParams()
        val jsObj: JsonObject = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "status_change")
        jsObj.addProperty("username", GLOBAL.username)
        jsObj.addProperty("status_id", statusId)
        jsObj.addProperty("order_id", orderId)
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(API_URL, params, object: AsyncHttpResponseHandler() {
            override fun onStart() {
                super.onStart()
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onFinish() {
                super.onFinish()
                binding.progressBar.visibility = View.GONE
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }
                Log.d("result21", result.toString())
                finish()
                startActivity(Intent(applicationContext, MainActivity::class.java).putExtra("username", username))
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {}

        })
    }

    private fun getDetails(orderId: String?) {
        var client = AsyncHttpClient()
        var params = RequestParams()
        val jsObj: JsonObject = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "order_details")
        jsObj.addProperty("username", GLOBAL.username)
        jsObj.addProperty("order_id", orderId)
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(API_URL, params, object: AsyncHttpResponseHandler() {

            override fun onStart() {
                super.onStart()
                binding.swiperefresh.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onFinish() {
                super.onFinish()
                binding.swiperefresh.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }

                Log.d("orders21", result.toString())
                try {
                    val mainJson = result?.let { JSONObject(it) }
                    val ordersJson = mainJson?.getJSONArray(Constant.ARRAY_NAME)
                    val orders = ArrayList<ItemOrder>()

                    var order: ItemOrder
                    if (ordersJson != null) {
                        for (i in 0 until ordersJson.length()) {
                            val orderJson = ordersJson.getJSONObject(i)

                            order = ItemOrder()
                            order.productName = orderJson.getString("food_name")
                            order.price = orderJson.getString("food_price")
                            order.quantity = orderJson.getString("quantity")
                            order.image = orderJson.getString("food_image")

                            itemOrder = ItemOrder()
                            itemOrder.customerName = orderJson.getString("customer_name")
                            itemOrder.customerAddress = orderJson.getString("customer_address")
                            itemOrder.cityName = orderJson.getString("city_name")
                            itemOrder.customerPhoneNumber = orderJson.getString("customer_phone_number")

                            itemOrder.restaurantName = orderJson.getString("restaurant_name")
                            itemOrder.restaurantAddress = orderJson.getString("restaurant_address")
                            itemOrder.restaurantPhone = orderJson.getString("restaurant_phone")
                            itemOrder.totalPrice = orderJson.getString("grand_total")

                            orders.add(order)
                        }
                    }

                    binding.cusName.text = itemOrder.customerName
                    binding.cusaddress1.text = itemOrder.customerAddress
                    binding.cusaddress2.text = itemOrder.cityName

                    binding.resName.text = itemOrder.restaurantName
                    binding.resaddress1.text = itemOrder.restaurantAddress
                    binding.resaddress2.text = itemOrder.cityName

                    binding.tvCustomerName.text = itemOrder.customerName
                    binding.tvOrderid.text = orderId
                    binding.tvprice.text = "Rs. " + itemOrder.totalPrice + "/-"

                    binding.cusCallBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.call), null,null, null)
                    binding.resCallBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.call), null,null, null)

                    binding.cusCallBtn.setOnClickListener {
                        dialNumber(itemOrder.customerPhoneNumber!!)
                    }

                    binding.resCallBtn.setOnClickListener {
                        dialNumber(itemOrder.restaurantPhone!!)
                    }

                    item = OrderItemAdapter(orders)
                    binding.rvorderitems.adapter = item

                }catch (e: Exception) {
                    Log.d("err", e.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
            }
        })
    }

    private fun dialNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    }

    fun onclickBack(view: View?) {
        onBackPressed()
    }
}