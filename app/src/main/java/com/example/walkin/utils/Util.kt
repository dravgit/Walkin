package com.example.walkin.utils

import android.widget.Toast
import androidx.annotation.StringRes
import com.example.walkin.R
import com.example.walkin.app.WalkinApplication

class Util() {
    companion object {
        fun showToast(@StringRes resId : Int) {
            Toast.makeText(WalkinApplication.appContext, WalkinApplication.appContext.getString(resId), Toast.LENGTH_LONG).show()
        }
    }
}