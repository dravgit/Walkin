package com.example.walkin.utils

import android.app.ProgressDialog
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.walkin.R
import com.example.walkin.app.WalkinApplication
import com.example.walkin.models.BaseResponseModel
import com.example.walkin.models.LoginResponseModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import kotlin.reflect.KClass


class NetworkUtil {
    companion object {
        private val STATUS_CODE_SUCCESS = 200
        private val STATUS_CODE_COMPANY_NOT_FOUND = 901
        private val STATUS_CODE_SEARIAL_NOT_FOUND = 902


        private val URL_DOMAIN = "http://165.22.250.233"
        private val URL_KACHEN_DOMAIN = "http://165.22.250.233"
        val URL_LOGIN = "$URL_DOMAIN/api/v1/login"
        val URL_CHECK_DEVICE = "$URL_KACHEN_DOMAIN/api/v1/checkdevice"
        val URL_GET_SUMMARY = ""
        val URL_SUBMIT = ""
        val URL_SEARCH = ""
        val URL_GET_LIST_DATA = ""

        var progressdialog : ProgressDialog? = null

        fun login(user: String, password: String, listener: NetworkLisener<LoginResponseModel>, kClass : Class<LoginResponseModel>) {
            showLoadingDialog()
            PreferenceUtils.setLoginUserName(user)
            PreferenceUtils.setLoginPassword(password)
            AndroidNetworking.post(URL_LOGIN)
                .addBodyParameter("username", user)
                .addBodyParameter("password", password)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(getResponseListener(kClass, listener))
        }

        fun checkDevice(companyId: String, listener: JSONObjectRequestListener) {
            AndroidNetworking.post(URL_CHECK_DEVICE)
                .addBodyParameter("serial_number", android.os.Build.SERIAL)
                .addBodyParameter("company_id", companyId)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.let {
                            val status = it.getInt("status_code")
                            if (STATUS_CODE_SUCCESS == status) {
                                if (PreferenceUtils.getToken().isNotEmpty()) {
                                    PreferenceUtils.setLoginSuccess()
                                }
                                listener.onResponse(response)
                            } else {
                                showError(status)
                            }
                        }
                        hideLoadingDialog()
                    }

                    override fun onError(anError: ANError?) {
                        hideLoadingDialog()
                        anError?.let {
                            showError(it.errorCode)
                        }
                    }
                })
        }

        fun loadSummaryData() {
//            AndroidNetworking.post(URL_LOGIN)
//                .addHeaders("Authorization", "Bearer "+ PreferenceUtils.getToken())
//                .addHeaders("Content-type", "application/json")
//                .addHeaders("Accept", "application/json")
//                .addBodyParameter("idcard", lendParamModel.idcard)
//                .addBodyParameter("customer_name", lendParamModel.customer_name)
//                .addBodyParameter("customer_address", lendParamModel.customer_address)
//                .addBodyParameter("customer_image", lendParamModel.customer_image)
//                .addBodyParameter("customer_phonenumber", lendParamModel.customer_phonenumber)
//                .addBodyParameter("product", Gson().toJson(lendParamModel.product))
//                .addBodyParameter("deadline", lendParamModel.deadline)
//                .addBodyParameter("user_id", PreferencesManager.getInstance().userId)
//                .addBodyParameter("tid", PreferencesManager.getInstance().tid)
//                .setTag("sellItem")
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(listener)

        }


        private fun showLoadingDialog() {
            progressdialog = ProgressDialog(WalkinApplication.appContext)
            progressdialog?.setMessage("Please Wait....")
            progressdialog?.show()
        }

        private fun hideLoadingDialog() {
            progressdialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
        }
        private fun <T : BaseResponseModel>getResponseListener(kClass: Class<T>, listener: NetworkLisener<T>): JSONObjectRequestListener {
            return object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        val status = it.getInt("status_code")
                        if (STATUS_CODE_SUCCESS.equals(status)) {
                            if (kClass.isInstance(LoginResponseModel::class.java)) {
                                updateInfo(it)
                                listener.onResponse(Gson().fromJson(it.toString(), kClass))
                            } else {
                                val jsonData = it.getJSONObject("data")
                                listener.onResponse(Gson().fromJson(jsonData.toString(), kClass))
                            }
                        } else {
                            showError(status)
                        }
                    }
                    hideLoadingDialog()
                }

                override fun onError(anError: ANError?) {
                    anError?.let {
                        showError(it.errorCode)
                    }
                    hideLoadingDialog()
                }
            }
        }

        private fun updateInfo(info: JSONObject) {
            val data = info.optJSONObject("data")
            val user = data?.optJSONObject("user")
            val userId = user?.getString("id")
            val userName = user?.getString("name")
            val company = data?.optJSONObject("company")
            val companyId = company?.getString("id")
            val companyName = company?.getString("name")
            val companyAddress = company?.getString("address")
            val companyPhone = company?.getString("phone")
            val companyEmail = company?.getString("email")
            val companyStatus = company?.getString("status")
            val signature =data?.optJSONArray("signature")
            val department =data?.optJSONArray("department")
            val objectiveType =data?.optJSONArray("objective_type")



        }

        fun showError(status: Int) {
            if (STATUS_CODE_COMPANY_NOT_FOUND == status) {
                Util.showToast(R.string.not_found_company)
            } else if (STATUS_CODE_SEARIAL_NOT_FOUND == status) {
                Util.showToast(R.string.not_found_serial)
            }
        }

        interface NetworkLisener<T> {
            fun onResponse(response: T)
        }
    }
}