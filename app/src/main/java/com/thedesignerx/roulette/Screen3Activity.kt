package com.thedesignerx.roulette

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_screen3.*
import kotlin.collections.ArrayList

class Screen3Activity : AppCompatActivity() {
    private var bettingAmount = 0
    private var gain = 0
    private var sessions = 0
    private var profit = 0
    private val list: MutableList<String> = ArrayList()

    private var bettingCurrency = ""

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
            Toast.makeText(this@Screen3Activity, getString(R.string.resetting), Toast.LENGTH_SHORT).show()
        }
        button_won.setOnClickListener {

        }
        button_lost.setOnClickListener {

        }
    }

    private fun populateData() {
        val bundle = intent.extras
        if (bundle != null) {

            bettingCurrency = bundle.getString(Screen2Activity.BETTING_CURRENCY)

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
        val listOfCurrencies = resources.getStringArray(R.array.currencies)
        when {
            bettingCurrency.equals(listOfCurrencies[1]) -> {
                textView_gain.text = bettingCurrency + gain.toString()
                textView_profit.text = bettingCurrency + profit.toString()
            }
            bettingCurrency.equals(listOfCurrencies[1]) -> {
                textView_gain.text = gain.toString() + bettingCurrency
                textView_profit.text = profit.toString() + bettingCurrency
            }
            bettingCurrency.equals(listOfCurrencies[2]) -> {
                textView_gain.text = gain.toString() + bettingCurrency
                textView_profit.text = profit.toString() + bettingCurrency
            }
            bettingCurrency.equals(listOfCurrencies[3]) -> {
                textView_gain.text = bettingCurrency + gain.toString()
                textView_profit.text = bettingCurrency + profit.toString()
            }
        }
        textView_sessions.text = sessions.toString()
        textView_betNumber.text = bettingAmount.toString()
        textView_betOn.text = RouletteUtils.getRandomElement(list)
    }
}