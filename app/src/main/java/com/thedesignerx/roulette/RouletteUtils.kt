package com.thedesignerx.roulette

import java.util.*

object RouletteUtils {
    fun getRandomElement(list: List<Int>): Int {
        val rand: Random = Random()
        return list.get(rand.nextInt(list.size))
    }
}