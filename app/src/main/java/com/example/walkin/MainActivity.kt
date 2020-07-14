package com.example.walkin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.centerm.smartpos.aidl.sys.AidlDeviceManager
import com.example.walkin.models.LoginResponseModel
import com.example.walkin.models.WalkInErrorModel
import com.example.walkin.utils.NetworkUtil
import com.example.walkin.utils.PreferenceUtils
import com.example.walkin.utils.Util
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : BaseActivity() {
    var btnLogin: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLogin = findViewById<View>(R.id.btnLogin) as Button
        btnLogin!!.setOnClickListener {
            login()
        }
    }

    override fun onDeviceConnected(deviceManager: AidlDeviceManager?) {

    }

    override fun onDeviceConnectedSwipe(manager: AidlDeviceManager?) {

    }

    fun login() {
        val userName = tVusername.text.toString()
        val userPassword = tVpassword.text.toString()

        if (userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this@MainActivity, "username and password is not empty", Toast.LENGTH_LONG).show()
        } else {
            NetworkUtil.login(userName, userPassword, object : NetworkUtil.Companion.NetworkLisener<LoginResponseModel> {
                override fun onResponse(response: LoginResponseModel) {
                    checkDevice(PreferenceUtils.getCompanyId())
                }

                override fun onError(errorModel: WalkInErrorModel) {
                    Toast.makeText(this@MainActivity, errorModel.msg, Toast.LENGTH_LONG).show()
                }

                override fun onExpired() {

                }
            }, LoginResponseModel::class.java)
        }
    }

    fun checkDevice(companyId: String) {
        NetworkUtil.checkDevice(companyId, object : JSONObjectRequestListener {
            override fun onResponse(response: JSONObject?) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                this@MainActivity.startActivity(intent)
            }

            override fun onError(anError: ANError?) {
                anError?.let {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}