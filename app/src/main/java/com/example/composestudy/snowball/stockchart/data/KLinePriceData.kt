package com.example.composestudy.snowball.stockchart.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author jingqingqing
 * @date 2021/8/21
 */
class KLinePriceData {
    @Expose
    @SerializedName("open")
    var mOpenPrice = 0f

    @Expose
    @SerializedName("high")
    var mMaxPrice = 0f

    @Expose
    @SerializedName("low")
    var mMinPrice = 0f

    @Expose
    @SerializedName("close")
    var mClosePrice = 0f
}