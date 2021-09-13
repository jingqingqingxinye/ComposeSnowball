package com.example.composestudy.snowball.stockchart.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author jingqingqing
 * @date 2021/8/21
 */
class MinuteTimePriceData {
    @Expose
    @SerializedName("max")
    var mMaxPrice = 0f

    @Expose
    @SerializedName("min")
    var mMinPrice = 0f

    @Expose
    @SerializedName("items")
    var priceList: List<Float>? = null
}