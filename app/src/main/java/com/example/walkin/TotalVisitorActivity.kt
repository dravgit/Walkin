package com.example.walkin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_detail.*

class TotalVisitorActivity : AppCompatActivity() {
    var statusCode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val myListData: Array<MyListData> = arrayOf<MyListData>(
                MyListData("Email", android.R.drawable.ic_dialog_email),
                MyListData("Info", android.R.drawable.ic_dialog_info),
                MyListData("Delete", android.R.drawable.ic_delete),
                MyListData("Dialer", android.R.drawable.ic_dialog_dialer),
                MyListData("Alert", android.R.drawable.ic_dialog_alert),
                MyListData("Map", android.R.drawable.ic_dialog_map),
                MyListData("Email", android.R.drawable.ic_dialog_email),
                MyListData("Info", android.R.drawable.ic_dialog_info),
                MyListData("Delete", android.R.drawable.ic_delete),
                MyListData("Dialer", android.R.drawable.ic_dialog_dialer),
                MyListData("Alert", android.R.drawable.ic_dialog_alert),
                MyListData("Map", android.R.drawable.ic_dialog_map))

        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        val adapter = MyListAdapter(myListData,statusCode)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        tVdetail.setText("จำนวนผู้ที่อยู่เกิน 1 วัน")
    }
}