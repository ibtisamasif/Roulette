package com.thedesignerx.roulette

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_screen2.*

class Screen2Activity : AppCompatActivity() {
    private val bettingAmount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen2)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        } else {
            initializeView()
        }
    }

    @TargetApi(23)
    private fun askPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
    }

    private fun initializeView() {
        imageView_closeButton.setOnClickListener {
            startService(Intent(this@Screen2Activity, FloatingViewService::class.java))
            finish()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.currencies))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_currency.adapter = adapter
        editText_bettingAmount.setText(bettingAmount.toString())
        button_ok.setOnClickListener {
            val intent = Intent(this@Screen2Activity, Screen3Activity::class.java)
            intent.putExtra(BETTING_AMOUNT, editText_bettingAmount.text.toString().toInt())
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                initializeView()
            } else {
                Toast.makeText(this, "Draw over other app permission not available. Closing the application", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startService(Intent(this@Screen2Activity, FloatingViewService::class.java))
        finish()
    }

    companion object {
        private const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
        const val BETTING_AMOUNT = "betting_amount"
    }
}