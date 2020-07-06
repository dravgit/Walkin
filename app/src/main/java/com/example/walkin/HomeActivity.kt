package com.example.walkin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnCheckin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, CheckInActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })
    }
}
