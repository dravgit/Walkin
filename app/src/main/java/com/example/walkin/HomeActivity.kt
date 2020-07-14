package com.example.walkin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkin.models.SummaryModel
import com.example.walkin.models.WalkInErrorModel
import com.example.walkin.utils.NetworkUtil
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnCheckin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, CheckInActivity::class.java)
            this@HomeActivity.startActivity(intent)
        })

        btnCheckout.setOnClickListener {
            val intent = Intent(this@HomeActivity, CheckOutActivity::class.java)
            this@HomeActivity.startActivity(intent)
        }

        btnRefresh.setOnClickListener {
            NetworkUtil.loadSummaryData(object : NetworkUtil.Companion.NetworkLisener<SummaryModel> {
                override fun onResponse(response: SummaryModel) {
                    setupView(response)
                }

                override fun onError(errorModel: WalkInErrorModel) {
                    Toast.makeText(this@HomeActivity, errorModel.msg, Toast.LENGTH_LONG).show()
                }

                override fun onExpired() {
                    btnRefresh.callOnClick()
                }
            }, SummaryModel::class.java)
        }

    }

    fun setupView(response: SummaryModel) {

    }
}
