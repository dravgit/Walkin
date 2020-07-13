package com.example.walkin.utils

import android.app.ProgressDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.walkin.R
import com.example.walkin.app.WalkinApplication
import com.example.walkin.models.*
import com.google.gson.Gson
import org.json.JSONObject


class NetworkUtil {
    companion object {
        private val STATUS_CODE_SUCCESS = 200
        private val STATUS_CODE_COMPANY_NOT_FOUND = 901
        private val STATUS_CODE_SEARIAL_NOT_FOUND = 902
        private val URL_DOMAIN = "http://165.22.250.233"
        private val URL_KACHEN_DOMAIN = "http://165.22.250.233"
        val URL_LOGIN = "$URL_DOMAIN/api/v1/login"
        val URL_CHECK_DEVICE = "$URL_KACHEN_DOMAIN/api/v1/checkdevice"
        val URL_GET_SUMMARY = "$URL_DOMAIN/api/v1/sumary"
        val URL_SEARCH = "$URL_DOMAIN/api/v1/search/order"
        val URL_GET_LIST_DATA = "$URL_DOMAIN/api/v1/search/listbytype"
        val URL_CHECK_IN = "$URL_DOMAIN/api/v1/checkin"
        val URL_CHECK_OUT = "$URL_DOMAIN/api/v1/checkout"
        var progressdialog: ProgressDialog? = null

        fun login(user: String, password: String, listener: NetworkLisener<LoginResponseModel>, kClass: Class<LoginResponseModel>) {
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
                .setTag("checkDevice")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.let {
                            val status = it.getInt("status_code")
                            if (STATUS_CODE_SUCCESS == status) {
                                if (PreferenceUtils.getToken()
                                        .isNotEmpty()) {
                                    PreferenceUtils.setLoginSuccess()
                                }
                                listener.onResponse(response)
                            } else {
                                val error = ANError(it.getString("message"))
                                error.errorCode = status
                                listener.onError(error)
                                showError(status)
                            }
                        }
                        hideLoadingDialog()
                    }

                    override fun onError(anError: ANError?) {
                        hideLoadingDialog()
                        anError?.let {
                            showError(it.errorCode)
                            listener.onError(anError)
                        }
                    }
                })
        }

        fun loadSummaryData(listener: NetworkLisener<SummaryModel>, kClass: Class<SummaryModel>) {
            AndroidNetworking.post(URL_GET_SUMMARY)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.getToken())
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .addBodyParameter("company_id", PreferenceUtils.getCompanyId())
                .setTag("loadSummaryData")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(getResponseListener(kClass, listener))
        }

        fun getListByType(type: String, listener: NetworkLisener<PartialVisitorResponseModel>, kClass: Class<PartialVisitorResponseModel>) {
            AndroidNetworking.get(URL_GET_LIST_DATA)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.getToken())
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .addPathParameter("company_id", PreferenceUtils.getCompanyId())
                .addPathParameter("type", type)
                .addPathParameter("limit", "500")
                .addPathParameter("offset", "0")
                .setTag("getListByType")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(getResponseListener(kClass, listener))
        }

        fun searchByOrder(code: String, listener: NetworkLisener<VisitorResponseModel>, kClass: Class<VisitorResponseModel>) {
            AndroidNetworking.get(URL_SEARCH)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.getToken())
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .addPathParameter("company_id", PreferenceUtils.getCompanyId())
                .addPathParameter("contact_code", code)
                .setTag("searchByOrder")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(getResponseListener(kClass, listener))
        }

        //        CheckinParamModel.Builder("", "", "", "").idcard("").build()
        fun checkIn(param: CheckInParamModel, listener: NetworkLisener<CheckInResponseModel>, kClass: Class<CheckInResponseModel>) {
            AndroidNetworking.post(URL_CHECK_IN)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.getToken())
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .addBodyParameter("idcard", param.idcard)
                .addBodyParameter("name", param.name)
                .addBodyParameter("vehicle_id", param.vehicleId)
                .addBodyParameter("temperature", param.temperature)
                .addBodyParameter("department_id", param.departmentId)
                .addBodyParameter("objective_id", param.objectiveId)
                .addBodyParameter("images", param.images)
                .setTag("checkIn")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(getResponseListener(kClass, listener))
        }

        fun checkOut(code: String, listener: NetworkLisener<CheckOutResponseModel>, kClass: Class<CheckOutResponseModel>) {
            AndroidNetworking.post(URL_CHECK_OUT)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.getToken())
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .addBodyParameter("company_id", PreferenceUtils.getCompanyId())
                .addBodyParameter("contact_code", code)
                .setTag("checkOut")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(getResponseListener(kClass, listener))
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

        private fun <T : BaseResponseModel> getResponseListener(kClass: Class<T>, listener: NetworkLisener<T>): JSONObjectRequestListener {
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
                            val obj = JSONObject().put("error_code", status)
                                .put("msg", it.getString("message"))
                            listener.onError(obj)
                            showError(status)
                        }
                    }
                    hideLoadingDialog()
                }

                override fun onError(anError: ANError?) {
                    anError?.let {
                        showError(it.errorCode)
                        val obj = JSONObject().put("error_code", it.errorCode)
                            .put("msg", it.message)
                        listener.onError(obj)
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
            val signature = data?.optJSONArray("signature")
            val department = data?.optJSONArray("department")
            val objectiveType = data?.optJSONArray("objective_type")
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
            fun onError(jsonObject: JSONObject)
        }
    }
}