package com.example.walkin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
//        edt_code.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(editText: Editable?) {
//                enableButtonSearch()
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//        })
        search.setOnClickListener {
            goBackWithCode(edt_code.text.toString())
        }
    }

    fun enableButtonSearch() {
        val isReady = edt_code.text.toString().length > 6
        search.isEnabled = isReady
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        goBackWithCode(rawResult.text)
    }

    fun goBackWithCode(code: String) {
        val intent = Intent()
        intent.putExtra("barcode", code)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
