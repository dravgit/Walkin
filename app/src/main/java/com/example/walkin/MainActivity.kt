package com.example.walkin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.walkin.models.LoginResponseModel
import com.example.walkin.models.WalkInErrorModel
import com.example.walkin.utils.NetworkUtil
import com.example.walkin.utils.PreferenceUtils
import com.example.walkin.utils.Util.Companion.setContext
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var btnLogin: Button? = null
    val PERMISSIONS_REQUEST_CODE = 103
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        setContext(this)
        btnLogin = findViewById<View>(R.id.btnLogin) as Button
        btnLogin!!.setOnClickListener {
            it.isEnabled = false
            login()
        }
        if (PreferenceUtils.isLoginSuccess()) {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            this@MainActivity.startActivity(intent)
            this@MainActivity.finish()
        }

    }
    private fun setupPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this,
                                                           Manifest.permission.CAMERA)
        val readPermission = ContextCompat.checkSelfPermission(this,
                                                           Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this,
                                                           Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
            readPermission != PackageManager.PERMISSION_GRANTED ||
            writePermission != PackageManager.PERMISSION_GRANTED ) {
            Log.i("CAMERA_REQUEST_CODE", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                                          arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                          PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("CAMERA_REQUEST_CODE", "Permission has been denied by user")
                    this.finish()
                } else {
                    Log.i("CAMERA_REQUEST_CODE", "Permission has been granted by user")
                }
            }
        }
    }

    fun login() {
        val userName = tVusername.text.toString()
        val userPassword = tVpassword.text.toString()

        if (userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this@MainActivity, "username and password is not empty", Toast.LENGTH_LONG).show()
        } else {
            reLogin(userName,userPassword)
        }
    }

    fun reLogin(userName: String,userPassword: String){
        NetworkUtil.login(userName, userPassword, object : NetworkUtil.Companion.NetworkLisener<LoginResponseModel> {
            override fun onResponse(response: LoginResponseModel) {
                checkDevice(PreferenceUtils.getCompanyId())
            }

            override fun onError(errorModel: WalkInErrorModel) {
                btnLogin?.isEnabled = true
                Toast.makeText(this@MainActivity, errorModel.msg, Toast.LENGTH_LONG).show()
            }

            override fun onExpired() {
                reLogin(userName,userPassword)
            }
        }, LoginResponseModel::class.java)
    }

    fun checkDevice(companyId: String) {
        NetworkUtil.checkDevice(companyId, object : JSONObjectRequestListener {
            override fun onResponse(response: JSONObject?) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                this@MainActivity.startActivity(intent)
                this@MainActivity.finish()
            }

            override fun onError(anError: ANError?) {
                btnLogin?.isEnabled = true
                anError?.let {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}