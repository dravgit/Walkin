package com.cyp.walkin.cyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cyp.walkin.R
import com.cyp.walkin.cyp.models.SummaryModel
import com.cyp.walkin.cyp.models.WalkInErrorModel
import com.cyp.walkin.cyp.utils.NetworkUtil
import com.cyp.walkin.cyp.utils.PreferenceUtils
import com.vanstone.appsdk.client.ISdkStatue
import com.vanstone.l2.Common
import com.vanstone.trans.api.MagCardApi.*
import com.vanstone.trans.api.SystemApi
import com.vanstone.trans.api.constants.GlobalConstants
import com.vanstone.utils.CommonConvert
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseKioskActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        logout.setOnClickListener {
            PreferenceUtils.setLoginFail()
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            this@HomeActivity.startActivity(intent)
            this@HomeActivity.finish()
        }
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

        btnCheckout.setOnClickListener {
            val intent = Intent(this@HomeActivity, CheckOutActivity::class.java)
            this@HomeActivity.startActivity(intent)
        }

        btnRefresh.setOnClickListener {
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
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
    fun setupView(response: SummaryModel) {
        tv_number_in.setText(response.total_in)
        tv_number_out.setText(response.total_out)
        tv_number_stay.setText(response.total_not_out)
        tv_number_more_one.setText(response.total_over)
    }

    override fun onBackPressed() {}
}
