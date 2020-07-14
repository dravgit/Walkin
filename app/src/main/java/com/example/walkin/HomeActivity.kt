package com.example.walkin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.list_item.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnCheckin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, CheckInActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
        btnCheckout.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, CheckOutActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
        btnTotalCheckin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, TotalCheckInActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
        btnTotalCheckout.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, TotalCheckOutActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
        btnTotalRemain.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, TotalRemainActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
        btnTotalVisitor.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, TotalVisitorActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
    }
}
