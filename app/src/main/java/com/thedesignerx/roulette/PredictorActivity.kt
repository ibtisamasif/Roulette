package com.thedesignerx.roulette

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_predictor.*

class PredictorActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predictor)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        bundle = intent.extras
        if (bundle != null) {
            profit = getProfitFromStorage()
            bettingAmount = bundle!!.getInt(SettingsActivity.BETTING_AMOUNT)
            bettingCurrency = bundle!!.getString(SettingsActivity.BETTING_CURRENCY)
            isResetTrue = bundle!!.getBoolean(SettingsActivity.IS_RESET_TRUE)
            initVariables()
            setListeners()
            populateData()
        }
    }

    private fun initVariables() {
        gain = 0
        sessions += 1
        finalBet = bettingAmount
    }

    private fun setListeners() {
        imageView_settingButton.setOnClickListener { finish() }
        imageView_closeButton.setOnClickListener {
            startService(Intent(this@PredictorActivity, FloatingWidgetService::class.java))
            finish()
            SettingsActivity.fa.finish()
        }
        imageView_reset.setOnClickListener {
            if (profit > 0) {
                profit -= lastGain
                sessions += 1
                updateUi()
                Toast.makeText(this@PredictorActivity, getString(R.string.resetting), Toast.LENGTH_SHORT).show()
            }
        }
        button_won.setOnClickListener {
            if (gain < 0) {
                if (finalBet < (gain * -1)) {
                    gain += finalBet
                    finalBet = (gain * -1) + bettingAmount
                } else {
                    if (finalBet != (gain * -1)) {
                        sessions += 1
                        Toast.makeText(this@PredictorActivity, getString(R.string.session_completed).plus(Constants.SPACE_STRING).plus(bettingCurrency).plus(bettingAmount).plus(" amount has been added to your total profits."), Toast.LENGTH_SHORT).show()
                    }
                    lastGain = (finalBet - (gain * -1))
                    profit += lastGain
                    gain = 0
                    finalBet = bettingAmount
                }
            } else {
                lastGain = (finalBet - (gain * -1))
                profit += lastGain
                gain = 0
                sessions += 1
                finalBet = bettingAmount
                Toast.makeText(this@PredictorActivity, getString(R.string.session_completed).plus(Constants.SPACE_STRING).plus(bettingCurrency).plus(bettingAmount).plus(" amount has been added to your total profits."), Toast.LENGTH_SHORT).show()
            }
            updateUi()
        }
        button_lost.setOnClickListener {
            gain -= finalBet
            finalBet = bettingAmount * 1
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


    @SuppressLint("SetTextI18n")
    private fun updateUi() {

        val listOfCurrencies = resources.getStringArray(R.array.currencies)
        when (bettingCurrency) {
            listOfCurrencies[0] -> {
                textView_gain.text = gain.toString() + bettingCurrency
                textView_profit.text = profit.toString() + bettingCurrency
            }
            listOfCurrencies[1] -> {
                textView_gain.text = bettingCurrency + gain.toString()
                textView_profit.text = bettingCurrency + profit.toString()
            }
            listOfCurrencies[2] -> {
                textView_gain.text = gain.toString() + bettingCurrency
                textView_profit.text = profit.toString() + bettingCurrency
            }
            listOfCurrencies[3] -> {
                textView_gain.text = bettingCurrency + gain.toString()
                textView_profit.text = bettingCurrency + profit.toString()
            }
        }
        textView_sessions.text = sessions.toString()
        textView_betNumber.text = finalBet.toString()
        textView_betOn.text = RouletteUtils.getRandomElement(list)

        saveProfitToStorage(profit)
    }

    private fun saveProfitToStorage(profit: Int) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("profit", profit)
        editor.commit()
    }

    private fun getProfitFromStorage(): Int {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getInt("profit", 0)
    }
}