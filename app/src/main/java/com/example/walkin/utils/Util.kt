package com.example.walkin.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.walkin.R
import com.example.walkin.app.WalkinApplication

class Util() {
    companion object {
        var activityContext: Context? = null
        fun showToast(@StringRes resId : Int) {
            Toast.makeText(WalkinApplication.appContext, WalkinApplication.appContext.getString(resId), Toast.LENGTH_LONG).show()
        }

        fun setContext(context: Context) {
            activityContext = context
        }
    }
}