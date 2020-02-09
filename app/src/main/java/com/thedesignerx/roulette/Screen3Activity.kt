package com.thedesignerx.roulette

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_screen3.*
import kotlin.collections.ArrayList

class Screen3Activity : AppCompatActivity() {
    private var isResetTrue: Boolean = false
    private var bundle: Bundle? = null
    private var bettingAmount = 0
    private var gain = 0
    private var sessions = 0
    private var profit = 0
    private val list: MutableList<String> = ArrayList()
    private var bettingCurrency = ""
    private var lastGain = 0
    private var finalBet = 0
    var isNewSession = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen3)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        bundle = intent.extras
        if (bundle != null) {
            bettingAmount = bundle!!.getInt(Screen2Activity.BETTING_AMOUNT)
            bettingCurrency = bundle!!.getString(Screen2Activity.BETTING_CURRENCY)
            isResetTrue = bundle!!.getBoolean(Screen2Activity.IS_RESET_TRUE)
            setListeners()
            populateData()
        }
    }

    private fun setListeners() {
        imageView_settingButton.setOnClickListener { finish() }
        imageView_closeButton.setOnClickListener {
            startService(Intent(this@Screen3Activity, FloatingViewService::class.java))
            finish()
        }
        imageView_reset.setOnClickListener {
            // Reset current session and removes current gain from profit and adds one session count
            if (profit > 0) {
                profit -= lastGain
                sessions += 1
                updateUi()
                Toast.makeText(this@Screen3Activity, getString(R.string.resetting), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Screen3Activity, "profit < 0", Toast.LENGTH_SHORT).show()
            }
        }
        button_won.setOnClickListener {
            if (gain / bettingAmount <= -4) {
                //User is asked to bet 1x bet more that their current negative gain.
                finalBet = (gain * -1) + bettingAmount
                updateUi()
            } else if (gain / bettingAmount <= -2) {
                gain += bettingAmount
                finalBet = 2 * bettingAmount
                updateUi()
            } else if (gain / bettingAmount <= -1){
                //Session ends and gain(1xbet) is added to total profit
                isNewSession = true
                lastGain = bettingAmount
                profit += lastGain
                updateUi()
            }
            if (finalBet > gain) {
                //Session ends and gain(1xbet) is added to total profit
//                isNewSession = true
                lastGain = bettingAmount
                profit += lastGain
                updateUi()
            }
        }
        button_lost.setOnClickListener {
            finalBet = bettingAmount * 1
            gain -= bettingAmount
            updateUi()
        }
    }


    private fun populateData() {

        list.add(getString(R.string.betting_box_black))
        list.add(getString(R.string.betting_box_red))
        list.add(getString(R.string.betting_box_even))
        list.add(getString(R.string.betting_box_odd))
        list.add(getString(R.string.betting_box_1_to_18))
        list.add(getString(R.string.betting_box_19_to_36))

        if (isResetTrue) {
            gain = 0
            sessions = 0
            profit = 0
        }

        updateUi()
    }


    private fun updateUi() {
        if (isNewSession) {
            isNewSession = false
            //A new session starts and gain is reset to 0
            gain = 0
            sessions += 1

            //chk
            finalBet = bettingAmount
        }

//        else {
//            finalBet = bettingAmount * 2
//        }


        val listOfCurrencies = resources.getStringArray(R.array.currencies)
        when {
            bettingCurrency.equals(listOfCurrencies[0]) -> {
                textView_gain.text = gain.toString() + bettingCurrency
                textView_profit.text = profit.toString() + bettingCurrency
            }
            bettingCurrency.equals(listOfCurrencies[1]) -> {
                textView_gain.text = bettingCurrency + gain.toString()
                textView_profit.text = bettingCurrency + profit.toString()
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
        textView_betNumber.text = finalBet.toString()
        textView_betOn.text = RouletteUtils.getRandomElement(list)
    }
}