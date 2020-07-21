package com.example.walkin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.walkin.models.CheckOutResponseModel
import com.example.walkin.models.VisitorResponseModel
import com.example.walkin.models.WalkInErrorModel
import com.example.walkin.utils.NetworkUtil
import kotlinx.android.synthetic.main.activity_check_out.*

class CheckOutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val REQUEST_CODE = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        btnScan.setOnClickListener{
            val intent = Intent(this, ScanActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        okCheckout.setOnClickListener{
            if(tVcode != null){
                NetworkUtil.checkOut(tVcode.getText().toString(),object : NetworkUtil.Companion.NetworkLisener<CheckOutResponseModel>{
                    override fun onResponse(response: CheckOutResponseModel) {
                        Log.e("Status","SUCCESS")
                    }

                    override fun onError(errorModel: WalkInErrorModel) {
                        Log.e("Status","ERROR")
                    }

                    override fun onExpired() {
                        okCheckout.callOnClick()
                    }

                }, CheckOutResponseModel::class.java)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check that it is the SecondActivity with an OK result
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val code = data!!.getStringExtra("barcode")
                searchData(code)
            }
        }
    }

    fun searchData(code: String){
        NetworkUtil.searchByOrder(code, object : NetworkUtil.Companion.NetworkLisener<VisitorResponseModel>{
            override fun onResponse(response: VisitorResponseModel) {
                var data = response
                var list = data.images
                Log.e("LIST",list.toString())
                var i = 0
                tVname.setText(data.name())
                tVidcard.setText(data.idcard())
                tVcar.setText(data.vehicle_id())
                tVtemperate.setText(data.temperature())
                tVdepartment.setText(data.department())
                tVobjective.setText(data.objective())
                tVcheckintime.setText(data.checkin_time())
                tVcode.setText(data.contact_code)
                imgVcar.setBackground(null)
                imgVperson.setBackground(null)
                for (i in i..list.size-1)
                {
                    if(list[i].type == "1"){
                        Glide.with(this@CheckOutActivity)
                                .load(list[i].url)
                                .placeholder(android.R.color.background_light)
                                .error(android.R.color.background_light)
                                .into(imgVperson)
                    }else if(list[i].type == "4"){
                        Glide.with(this@CheckOutActivity)
                                .load(list[i].url)
                                .placeholder(android.R.color.background_light)
                                .error(android.R.color.background_light)
                                .into(imgVperson)
                    }
                    if(list[i].type == "2"){
                        Glide.with(this@CheckOutActivity)
                                .load(list[i].url)
                                .placeholder(android.R.color.background_light)
                                .error(android.R.color.background_light)
                                .into(imgVcar)
                    }
                }
            }

            override fun onError(errorModel: WalkInErrorModel) {
                Log.e("CHECK",errorModel.toString())
            }

            override fun onExpired() {
                searchData(code)
            }
        },VisitorResponseModel::class.java)
    }
}
