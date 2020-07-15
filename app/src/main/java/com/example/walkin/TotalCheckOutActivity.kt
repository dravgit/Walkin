package com.example.walkin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.walkin.models.PartialVisitorResponseModel
import com.example.walkin.models.WalkInErrorModel
import com.example.walkin.utils.NetworkUtil
import kotlinx.android.synthetic.main.activity_detail.*


class TotalCheckOutActivity : AppCompatActivity() {
    var statusCode = true
    lateinit var adapter : DetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        adapter = DetailAdapter(this@TotalCheckOutActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        tVdetail.setText("จำนวนผู้ออกจากตึก")
        btnRefresh.setOnClickListener {
            loadData()
        }
        loadData()
    }

    fun loadData() {
        NetworkUtil.getListByType(NetworkUtil.STATUS_TYPE_OUT, object : NetworkUtil.Companion.NetworkLisener<List<PartialVisitorResponseModel>> {
            override fun onResponse(response: List<PartialVisitorResponseModel>) {
                adapter.setListdata(response)
            }

            override fun onError(errorModel: WalkInErrorModel) {

            }

            override fun onExpired() {
                loadData()
            }
        })
    }
}