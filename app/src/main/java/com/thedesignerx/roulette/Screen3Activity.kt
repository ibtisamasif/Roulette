package com.thedesignerx.roulette

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_screen3.*

class Screen3Activity : AppCompatActivity() {
    private var bettingAmount = 0
    private var gain = 0
    private var sessions = 0
    private var profit = 0
    private val list: MutableList<String> = ArrayList()

    private var lastGain = 0

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
        imageView_reset.setOnClickListener {
            // Reset current session and removes current gain from profit and adds one session count
            profit -= lastGain
            sessions += 1
            updateUi()
            Toast.makeText(this@Screen3Activity, "Resetting", Toast.LENGTH_SHORT).show()
        }
        button_won.setOnClickListener { Toast.makeText(this@Screen3Activity, "Won pressed", Toast.LENGTH_SHORT).show() }
        button_lost.setOnClickListener { Toast.makeText(this@Screen3Activity, "Lost pressed", Toast.LENGTH_SHORT).show() }
    }

    private fun populateData() {
        val bundle = intent.extras
        if (bundle != null) {

            list.add(getString(R.string.betting_box_black))
            list.add(getString(R.string.betting_box_red))
            list.add(getString(R.string.betting_box_even))
            list.add(getString(R.string.betting_box_odd))
            list.add(getString(R.string.betting_box_1_to_18))
            list.add(getString(R.string.betting_box_19_to_36))

            val isResetTrue = bundle.getBoolean(Screen2Activity.IS_RESET_TRUE)
            if (isResetTrue) {
                gain = 0
                sessions = 0
                profit = 0
            }

            bettingAmount = bundle.getInt(Screen2Activity.BETTING_AMOUNT)

            updateUi()
        }
    }

    private fun updateUi() {
        textView_gain.text = gain.toString()
        textView_sessions.text = sessions.toString()
        textView_profit.text = profit.toString()
        textView_betNumber.text = bettingAmount.toString()
        textView_betOn.text = RouletteUtils.getRandomElement(list)
    }
}