package com.thedesignerx.roulette

import java.util.*

object RouletteUtils {
    fun getRandomElement(list: List<String>): String {
        val rand = Random()
        return list[rand.nextInt(list.size)]
    }
}