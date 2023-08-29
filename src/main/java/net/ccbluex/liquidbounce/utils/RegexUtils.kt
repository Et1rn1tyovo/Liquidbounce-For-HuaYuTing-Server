/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This part is taken from UnlegitMC/FDPClient. Please credit them when using this in your repository.
 */
package net.ccbluex.liquidbounce.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Matcher
import java.util.regex.Pattern

object RegexUtils {
    fun match(matcher: Matcher): Array<String> {
        val result = mutableListOf<String>()

        while (matcher.find()) {
            result.add(matcher.group())
        }

        return result.toTypedArray()
    }

    fun match(text: String, pattern: Pattern): Array<String> {
        return match(pattern.matcher(text))
    }

    fun match(text: String, pattern: String): Array<String> {
        return match(text, Pattern.compile(pattern))
    }

    /**
     * Rounds a double. From https://stackoverflow.com/a/2808648/9140494
     *
     * @param value  the value to be rounded
     * @param places Decimal places
     * @return The rounded value
     */
    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).toDouble()
    }
}