package com.example.composestudy.snowball.stockchart.utils

import androidx.compose.ui.input.pointer.PointerEvent
import java.text.DecimalFormat
import kotlin.math.sqrt

// 计算移动距离
fun distance(event: PointerEvent): Float {
    if (event.changes.size < 2) return 0f
    val x = event.changes.get(0).position.x - event.changes.get(1).position.x
    val y = event.changes.get(0).position.y - event.changes.get(1).position.y
    return sqrt((x * x + y * y).toDouble()).toFloat()
}

fun priceToY(f: Float, yMaxValue: Float, yMinValue: Float, height: Float): Float {
    return ((yMaxValue - f) / (yMaxValue - yMinValue) * height)
}

fun yToPrice(f: Float, yMaxValue: Float, yMinValue: Float, height: Float): Float {
    return yMaxValue - f * (yMaxValue - yMinValue) / height
}

fun getOffset(value: Float): Float {
    val str = value.toString()
    if (str.contains(".")) {
        val firstStr = str.subSequence(0, str.indexOf("."))
        return getOffsetValue(firstStr.toString())
    } else {
        return getOffsetValue(str)
    }
    return 0f
}

private fun getOffsetValue(str: String): Float {
    if (str.length > 3) {
        return 30f
    } else if (str.length > 1) {
        return 3f
    } else {
        return 0.3f
    }
}

fun numToString(f: Float): String? {
    val format = DecimalFormat("0.00")
    return format.format(f)
}

fun valueToString(value: Float): String {
    val decimalFormat = DecimalFormat("0.00")
    return  decimalFormat.format(value)
}

fun getPercentStr(value: Float, init: Float): String {
    val decimalFormat = DecimalFormat("0.00%")
    return  decimalFormat.format((value - init) / init)
}

