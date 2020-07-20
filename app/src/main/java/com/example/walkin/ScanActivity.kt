package com.example.walkin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA),5)
            }
        }
        setContentView(R.layout.activity_scan)
        search.setOnClickListener {
            goBackWithCode(edt_code.text.toString())
        }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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
