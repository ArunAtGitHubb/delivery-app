package com.jellysoft.deliveryapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jellysoft.deliveryapp.databinding.ActivitySplashBinding
import com.jellysoft.deliveryapp.db.DeliveryDbHelper
import com.jellysoft.deliveryapp.util.DeliveryReaderContract.SQL_CREATE_TABLE_DELIVERY

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        val dbHelper = DeliveryDbHelper(applicationContext)
        val db = dbHelper.writableDatabase
        db.execSQL(SQL_CREATE_TABLE_DELIVERY)

        val cursor = db.rawQuery("SELECT user_name FROM delivery", null)
        if(cursor.count > 0){
            cursor.moveToFirst()
            val username = cursor.getString(0)
            cursor.close()
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(applicationContext, MainActivity::class.java).putExtra("username", username))
            }, 1000)
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
            }, 1000)
        }

    }
}
