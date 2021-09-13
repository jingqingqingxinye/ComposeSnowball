package com.example.composestudy.snowball.stockchart.views

import android.text.TextPaint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.composestudy.snowball.stockchart.data.MinuteTimePriceData
import com.example.composestudy.snowball.stockchart.utils.getPercentStr
import com.example.composestudy.snowball.stockchart.utils.priceToY
import com.example.composestudy.snowball.stockchart.utils.valueToString
import com.example.composestudy.snowball.stockchart.utils.yToPrice
import com.example.composestudy.ui.*
import kotlin.math.floor


private val CHART_BOTTOM_BAR_HEIGHT = 30.dp

@Composable
fun MinuteTimeView(data: MinuteTimePriceData?) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth()) {
        ChartHeadView()
        MinuteTimeChart(data)
    }
}

@Composable
private fun MinuteTimeChart(data: MinuteTimePriceData?) {
    if (data?.priceList == null || data.priceList?.size == 0) {
        return
    }
    val list: List<Float> = data.priceList!!
    val maxYValue = data.mMaxPrice
    val minYValue = data.mMinPrice
    val linePaint = Paint()
    linePaint.isAntiAlias = true
    linePaint.style = PaintingStyle.Stroke
    linePaint.strokeWidth = 1f
    linePaint.color = Color.LightGray

    val textPaint = TextPaint()
    textPaint.isAntiAlias = true
    textPaint.color = Red_F54346.toArgb()
    textPaint.textSize = 30f

    val crossPaint = Paint()
    crossPaint.isAntiAlias = true
    crossPaint.style = PaintingStyle.Stroke
    crossPaint.strokeWidth = 3f
    crossPaint.color = Color.Black


    val initValue = list.get(0)
    val yValueInterval = (maxYValue - minYValue) / DIVIDER_NUM

    val shadowPath = Path()
    var isShowCross by remember{ mutableStateOf(false) }
    var downX by remember { mutableStateOf(0f) }
    var crossX by remember { mutableStateOf(0f) }
    var crossY by remember { mutableStateOf(0f) }

    var width = 0f
    var height = 0f
    var frameHeight = 0f
    var yInterval = 0f
    var widthInterval = floor(width / 330)// 9:30-15:00 = 5 * 60 + 30 = 330min

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(CHART_HEIGHT + CHART_BOTTOM_BAR_HEIGHT)
        .padding(CHART_PADDING)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                if (isShowCross) {
                    if (crossX > widthInterval * list.size) {
                        crossX = widthInterval * list.size
                    }
                    drawIntoCanvas {
                        // 绘制十字交叉线
                        val priceStr =
                            yToPrice(crossY, maxYValue, minYValue, frameHeight).toString()
                        val textWidth = textPaint.measureText(priceStr)
                        it.drawLine(
                            Offset(0f, crossY),
                            Offset(width - textWidth, crossY),
                            crossPaint
                        )
                        it.drawLine(Offset(crossX, 0f), Offset(crossX, frameHeight), crossPaint)
                        // 绘制交叉线上的价格
                        textPaint.color = Color.Blue.toArgb()
                        it.nativeCanvas.drawText(priceStr, width - textWidth, crossY, textPaint)
                    }
                }
            }
        }
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    while (true) {
                        var event = awaitPointerEvent(PointerEventPass.Final)
                        if (event.changes.size == 1) {
                            val pointer = event.changes[0]
                            if (!pointer.pressed) {
                                // 手指抬起
                                break
                            } else {
                                if (pointer.previousPressed
                                    && Math.abs(pointer.previousUptimeMillis - pointer.uptimeMillis)
                                    > viewConfiguration.longPressTimeoutMillis
                                ) {
                                    // 长按
                                    isShowCross = true
                                    crossX = pointer.position.x
                                    crossY = pointer.position.y
                                } else if (isShowCross && pointer.previousPressed) {
                                    // 显示坐标后 拖动
                                    crossX = pointer.position.x
                                    crossY = pointer.position.y
                                } else if (!pointer.previousPressed) {
                                    downX = pointer.position.x
                                    if (isShowCross) {
                                        isShowCross = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
    {
        drawIntoCanvas {
            width = drawContext.size.width
            height = drawContext.size.height
            frameHeight = height - 30.dp.toPx()
            yInterval = frameHeight / DIVIDER_NUM
            // 9:30-15:00 = 5 * 60 + 30 = 330min
            widthInterval = floor(width / 330)
            // 1.绘制等分线
            for (i in 0..DIVIDER_NUM) {
                it.drawLine(
                    Offset(0f, 0f + i * yInterval),
                    Offset(width, 0f + i * yInterval), linePaint
                )
            }
            // 2.绘制x轴时间值
            textPaint.color = Color.Black.toArgb()
            it.nativeCanvas.drawText("9:00", 0f, height - textPaint.textSize, textPaint)
            it.nativeCanvas.drawText("15:00", width - textPaint.measureText("15:00"),
                height - 8.dp.toPx(), textPaint)
            // 3.绘制股价走势折线
            linePaint.color = Color.Blue
            shadowPath.moveTo(0f, frameHeight)
            for (i in list.indices) {
                if (i + 1 >= list.size) {
                    break
                }
                val y1 = priceToY(list[i], maxYValue, minYValue, frameHeight)
                val y2 = priceToY(list[i + 1], maxYValue, minYValue, frameHeight)
                val x1 = i * widthInterval
                val x2 = (i + 1) * widthInterval
                it.drawLine(
                    Offset( x1, y1),
                    Offset(x2, y2), linePaint)

                // 4.绘制阴影
                shadowPath.lineTo(x2, y2)
                if (i + 1 == list.size - 1) {
                    shadowPath.lineTo(x2, frameHeight)
                    shadowPath.close()
                    linePaint.color = Color_dfecfe
                    linePaint.style = PaintingStyle.Fill
                    it.drawPath(shadowPath, linePaint)
                }
            }

            // 6.最后绘制y轴坐标，防止被阴影盖住
            for (i in 0..DIVIDER_NUM) {
                val resultValue = valueToString(maxYValue - yValueInterval * i)
                if (resultValue == valueToString(initValue)) {
                    textPaint.color = Color.Black.toArgb()
                } else if (resultValue > valueToString(initValue)) {
                    textPaint.color = Red_F54346.toArgb()
                } else if (resultValue < valueToString(initValue)){
                    textPaint.color = Green_14BB71.toArgb()
                }
                it.nativeCanvas.drawText(resultValue,
                    0f, textPaint.textSize + i * yInterval, textPaint)
                it.nativeCanvas.drawText(
                    getPercentStr(maxYValue - i * yValueInterval, initValue),
                    width - 36.dp.toPx(), textPaint.textSize + i * yInterval, textPaint)
            }
        }
    }
}