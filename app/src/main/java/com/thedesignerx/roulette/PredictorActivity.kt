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
    private var bettingAmount = 0
    private var gain = 0
    private var sessions = 1
    private var profit = 0
    private val list: MutableList<String> = ArrayList()
    private var bettingCurrency = ""
    private var lastGain = 0
    private var finalBet = 0
    private lateinit var randomValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predictor)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        bettingCurrency = getCurrencySymbolFromStorage()
        bettingAmount = getBettingAmountFromStorage()

        sessions = getSessionFromStorage()
        gain = getGainFromStorage()

        if (bettingAmount == 0) {
            val intent = Intent(this@PredictorActivity, SettingsActivity::class.java)
            intent.putExtra(SettingsActivity.BETTING_AMOUNT, bettingAmount)
            startActivityForResult(intent, 100)
        }
        populateData()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 100) {
            if (data != null) {
                isResetTrue = data.getBooleanExtra(SettingsActivity.IS_RESET_TRUE, false)

                bettingCurrency = data.getStringExtra(SettingsActivity.BETTING_CURRENCY)
                if (bettingCurrency != getCurrencySymbolFromStorage()) {
                    saveCurrencySymbolToStorage(bettingCurrency)
                }

                bettingAmount = data.getIntExtra(SettingsActivity.BETTING_AMOUNT, 0)
                if (bettingAmount != getBettingAmountFromStorage()) {
                    gain = 0
                    saveBettingAmountToStorage(bettingAmount)
                }
                populateData()
            }
        }
    }

    private fun setListeners() {
        imageView_settingButton.setOnClickListener {
            val intent = Intent(this@PredictorActivity, SettingsActivity::class.java)
            intent.putExtra(SettingsActivity.BETTING_AMOUNT, bettingAmount)
            startActivityForResult(intent, 100)
        }
        imageView_closeButton.setOnClickListener {
            startService(Intent(this@PredictorActivity, FloatingWidgetService::class.java))
            finish()
        }
        imageView_reset.setOnClickListener {
            gain = 0
            updateUi()
        }
        button_won.setOnClickListener {
            if (gain < 0) {
                if (finalBet < (gain * -1)) {
                    val lastGain = gain
                    gain += finalBet
                    finalBet = if ((lastGain * -1) >= bettingAmount * 8) ((lastGain * -1)) / 2 //If the current sessions gain is -8x bet or more and you get one bet correct, the next bet will be half the amount of current negative bets instead of 1x more than the current negative like it is now.
                    else (lastGain * -1)
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

            randomValue = RouletteUtils.getRandomElement(list)
            updateUi()
        }
        button_lost.setOnClickListener {
            gain -= finalBet
            finalBet = bettingAmount * 1

            randomValue = RouletteUtils.getRandomElement(list)
            updateUi()
        }
    }


    private fun populateData() {
        finalBet = bettingAmount

        profit = getProfitFromStorage()
        sessions = getSessionFromStorage()
        gain = getGainFromStorage()
        randomValue = getOnbetFromStorage()


        list.add(getString(R.string.betting_box_black))
        list.add(getString(R.string.betting_box_red))
        list.add(getString(R.string.betting_box_even))
        list.add(getString(R.string.betting_box_odd))
        list.add(getString(R.string.betting_box_1_to_18))
        list.add(getString(R.string.betting_box_19_to_36))

        if (isResetTrue) {
            gain = 0
            sessions = 1
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
        textView_betOn.text = randomValue
        when (randomValue) {
            getString(R.string.betting_box_black) -> textView_betOn.setBackgroundResource(R.drawable.ic_background_black_round)
            getString(R.string.betting_box_red) -> textView_betOn.setBackgroundResource(R.drawable.ic_background_red_round)
            getString(R.string.betting_box_odd), getString(R.string.betting_box_even), getString(R.string.betting_box_1_to_18), getString(R.string.betting_box_19_to_36) -> textView_betOn.setBackgroundResource(R.color.colorTransparent)
        }
        saveProfitToStorage(profit)
        saveSessionToStorage(sessions)
        saveGainToStorage(gain)
        saveOnbetToStorage(randomValue)
    }

    private fun saveBettingAmountToStorage(amount: Int) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(SettingsActivity.BETTING_AMOUNT, amount)
        editor.commit()
    }

    private fun getBettingAmountFromStorage(): Int {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getInt(SettingsActivity.BETTING_AMOUNT, 0)
    }

    private fun saveProfitToStorage(profit: Int) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(PROFIT, profit)
        editor.commit()
    }

    private fun getProfitFromStorage(): Int {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getInt(PROFIT, 0)
    }

    private fun saveCurrencySymbolToStorage(symbol: String) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(SettingsActivity.BETTING_CURRENCY, symbol)
        editor.commit()
    }

    private fun getCurrencySymbolFromStorage(): String {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getString(SettingsActivity.BETTING_CURRENCY, "")
    }

    private fun saveSessionToStorage(sessions: Int) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(SESSIONS, sessions)
        editor.commit()
    }

    private fun getSessionFromStorage(): Int {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getInt(SESSIONS, 0)
    }

    private fun saveGainToStorage(gain: Int) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(GAIN, gain)
        editor.commit()
    }

    private fun getGainFromStorage(): Int {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getInt(GAIN, 0)
    }


    private fun saveOnbetToStorage(onbet: String) {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(ONBET, onbet)
        editor.commit()
    }

    private fun getOnbetFromStorage(): String {
        val sp = getSharedPreferences("roulette", Activity.MODE_PRIVATE)
        return sp.getString(ONBET, "")
    }


    companion object {
        const val PROFIT = "profit"
        const val SESSIONS = "sessions"
        const val GAIN = "gain"
        const val ONBET = "onbet"

    }
}