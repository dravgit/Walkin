package com.example.walkin.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.walkin.R
import com.example.walkin.app.WalkinApplication
import java.text.SimpleDateFormat
import java.util.*

class Util() {
    companion object {
        var activityContext: Context? = null
        fun showToast(@StringRes resId : Int) {
            Toast.makeText(WalkinApplication.appContext, WalkinApplication.appContext.getString(resId), Toast.LENGTH_LONG).show()
        }

        fun setContext(context: Context) {
            activityContext = context
        }

        fun toDateFormat(date: String): String {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
            val output: String = formatter.format(parser.parse(date))
            val date = toDateTh(output)
            return ""+date.get(Calendar.DATE)+" "+ getMonth(date)+" "+(date.get(Calendar.YEAR)+543) +" "+ date.get(Calendar.HOUR)+":"+date.get(Calendar.MINUTE)
        }

        fun getMonth(c: Calendar): String {
            val Months= arrayListOf( "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.",
                                     "พ.ค.", "มิ.ย.", "ก.ค.", "ส.ค.",
                                     "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค.")
            var month = c.get(Calendar.MONTH)

            return Months[month]
        }

        fun toDateTh(date: String): Calendar {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
            cal.time = sdf.parse(date)
            return cal
        }
    }
}