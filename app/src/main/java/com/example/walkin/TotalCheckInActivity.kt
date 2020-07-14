package com.example.walkin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_detail.*

class TotalCheckInActivity : AppCompatActivity() {
    var statusCode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val myListData: Array<DetailListData> = arrayOf<DetailListData>(
                DetailListData("Email", android.R.drawable.ic_dialog_email),
                DetailListData("Info", android.R.drawable.ic_dialog_info),
                DetailListData("Delete", android.R.drawable.ic_delete),
                DetailListData("Dialer", android.R.drawable.ic_dialog_dialer),
                DetailListData("Alert", android.R.drawable.ic_dialog_alert),
                DetailListData("Map", android.R.drawable.ic_dialog_map),
                DetailListData("Email", android.R.drawable.ic_dialog_email),
                DetailListData("Info", android.R.drawable.ic_dialog_info),
                DetailListData("Delete", android.R.drawable.ic_delete),
                DetailListData("Dialer", android.R.drawable.ic_dialog_dialer),
                DetailListData("Alert", android.R.drawable.ic_dialog_alert),
                DetailListData("Map", android.R.drawable.ic_dialog_map))

        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        val adapter = DetailAdapter(myListData, statusCode)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        tVdetail.setText("จำนวนผู้เข้าตึก")
    }
}