package com.lonebytesoft.hamster.pokemongo.js

import kotlin.js.asDynamic

internal inline fun <T> T?.aggregate(value: T?, aggregator: (T, T) -> T): T? =
        if ((this != null) && (value != null)) aggregator(this, value) else null

internal fun Double?.isGreaterZero(): Boolean = (this ?: Double.MAX_VALUE) > 0.0

internal fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits) as String

internal fun String?.orUnknownDistance(): String = if (this == null) "<span class='fail'>Unknown</span>" else this + " km"

internal fun String.appendLineBreak(): String = if (isNotEmpty()) this + "<br/>" else this
