package com.thedesignerx.roulette

import java.util.*

object RouletteUtils {
    fun getRandomElement(list: List<Int>): Int {
        val rand = Random()
        return list[rand.nextInt(list.size)]
    }
}