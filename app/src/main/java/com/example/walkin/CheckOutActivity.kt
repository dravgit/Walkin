package com.example.walkin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.centerm.smartpos.aidl.sys.AidlDeviceManager
import com.example.walkin.models.CheckOutResponseModel
import com.example.walkin.models.VisitorResponseModel
import com.example.walkin.models.WalkInErrorModel
import com.example.walkin.utils.NetworkUtil
import kotlinx.android.synthetic.main.activity_check_out.*

class CheckOutActivity : BaseActivity() {

    var alreadyOut = false
    override fun onPrintDeviceConnected(manager: AidlDeviceManager?) {
    }

    override fun onDeviceConnected(deviceManager: AidlDeviceManager?) {
    }

    override fun onDeviceConnectedSwipe(manager: AidlDeviceManager?) {
    }

    override fun showMessage(str: String?, black: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val REQUEST_CODE = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        btnScan.setOnClickListener{
            val intent = Intent(this, ScanActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        cancleCheckout.setOnClickListener{
            this@CheckOutActivity.finish()
        }
        okCheckout.setOnClickListener{
            if(tVcode != null) {
                if (!alreadyOut) {
                    NetworkUtil.checkOut(tVcode.getText()
                                             .toString(), object : NetworkUtil.Companion.NetworkLisener<CheckOutResponseModel> {
                        override fun onResponse(response: CheckOutResponseModel) {
                            Log.e("Status", "SUCCESS")
                            this@CheckOutActivity.finish()
                        }

                        override fun onError(errorModel: WalkInErrorModel) {
                            Log.e("Status", "ERROR")
                            checkError(errorModel)
                        }

                        override fun onExpired() {
                            okCheckout.callOnClick()
                        }
                    }, CheckOutResponseModel::class.java)
                } else {
                    Toast.makeText(this, "ออกไปแล้ว", Toast.LENGTH_LONG).show()
                }
            }
        }

        val intent = Intent(this, ScanActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
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
                alreadyOut = data.status.equals("1")
                var list = data.images
                Log.e("LIST",list.toString())
                tVname.setText(data.name())
                tVidcard.setText(data.idcard())
                tVcar.setText(data.vehicle_id())
                tVtemperate.setText(data.temperature())
                tVdepartment.setText(data.department())
                tVobjective.setText(data.objective())
                tVcheckintime.setText(data.checkin_time())
                tVcode.setText(data.contact_code)
                if (alreadyOut) {
                    okCheckout.visibility = View.GONE
                    headtVcheckouttime.visibility = View.VISIBLE
                    tVcheckouttime.setText(data.checkout_time)
                } else {
                    okCheckout.visibility = View.VISIBLE
                    headtVcheckouttime.visibility = View.GONE
                    tVcheckouttime.setText("")

                }
                if(list[0].url != ""){
                    imgVperson.setBackground(null)
                    Glide.with(this@CheckOutActivity)
                            .load(list[0].url)
                            .placeholder(android.R.color.background_light)
                            .error(android.R.color.background_light)
                            .into(imgVperson)
                }else if(list[3].url != ""){
                    imgVperson.setBackground(null)
                    Glide.with(this@CheckOutActivity)
                            .load(list[3].url)
                            .placeholder(android.R.color.background_light)
                            .error(android.R.color.background_light)
                            .into(imgVperson)
                }
                if(list[1].url != ""){
                    imgVcar.setBackground(null)
                    Glide.with(this@CheckOutActivity)
                            .load(list[1].url)
                            .placeholder(android.R.color.background_light)
                            .error(android.R.color.background_light)
                            .into(imgVcar)
                }
            }

            override fun onError(errorModel: WalkInErrorModel) {
                Log.e("CHECK",errorModel.toString())
                checkError(errorModel)
            }

            override fun onExpired() {
                searchData(code)
            }
        },VisitorResponseModel::class.java)
    }
}
