package com.thedesignerx.roulette

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_screen3.*

class Screen3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen3)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setListeners()
        populateData()
    }

    private fun setListeners() {
        imageView_settingButton.setOnClickListener { finish() }
        imageView_closeButton.setOnClickListener {
            startService(Intent(this@Screen3Activity, FloatingViewService::class.java))
            finish()
        }
        button_won.setOnClickListener { Toast.makeText(this@Screen3Activity, "Won pressed", Toast.LENGTH_SHORT).show() }
        button_lost.setOnClickListener { Toast.makeText(this@Screen3Activity, "Lost pressed", Toast.LENGTH_SHORT).show() }
    }

    private fun populateData() {
        val bundle = intent.extras
        if (bundle != null) {
            val bettingAmount = bundle.getInt(Screen2Activity.BETTING_AMOUNT)
            textView_betNumber.text = bettingAmount.toString()

            val list: MutableList<String> = ArrayList()
            list.add("Black")
            list.add("Red")
            list.add("Even")
            list.add("Odd")
            list.add("1-18")
            list.add("19-36")
            textView_betOn.text = RouletteUtils.getRandomElement(list)
        }
    }
}