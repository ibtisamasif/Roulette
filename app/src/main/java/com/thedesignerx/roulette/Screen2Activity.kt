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
        //Check if the application has draw over other apps permission or not?
//This permission is by default available for API<23. But for API > 23
//you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) { //If the draw over permission is not available open the settings screen
//to grant the permission.
            askPermission()
        } else {
            initializeView()
        }
    }

    @TargetApi(23)
    private fun askPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
    }

    /**
     * Set and initialize the view elements.
     */
    private fun initializeView() {
        imageView_closeButton.setOnClickListener {
            startService(Intent(this@Screen2Activity, FloatingViewService::class.java))
            finish()
        }
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, resources.getStringArray(R.array.currencies))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_currency.adapter = adapter
        editText_bettingAmount.setText(Integer.toString(bettingAmount))
        button_ok.setOnClickListener {
            val intent = Intent(this@Screen2Activity, Screen3Activity::class.java)
            intent.putExtra(BETTING_AMOUNT, bettingAmount)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) { //Check if the permission is granted or not.
            if (resultCode == Activity.RESULT_OK) {
                initializeView()
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show()
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
        private const val BETTING_AMOUNT = "betting_amount"
    }
}