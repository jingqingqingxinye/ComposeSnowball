package com.example.composestudy.snowball.stockchart.views


import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composestudy.R
import com.example.composestudy.snowball.stockchart.data.KLinePriceData
import com.example.composestudy.snowball.stockchart.utils.*
import com.example.composestudy.ui.*
import kotlin.math.abs


// 分割线，4等分
const val DIVIDER_NUM = 4
// 默认缩放比例
private const val SCALE_DEFAULT = 8f
// 最大缩放值
private const val SCALE_MAX = 25f
// 最小缩放值
private const val SCALE_MIN = 3f
// 缩放步长
private const val SCALE_STEP = 0.5f
private val CANDLE_DEFAULT_WIDTH = 0.5.dp
private val CANDLE_DEFAULT_SPACE_WIDTH = 0.1.dp
val CHART_HEIGHT = 300.dp
val CHART_PADDING = 10.dp


@Composable
fun KLineView(dataList: List<KLinePriceData>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth()) {
        ChartHeadView()
        KLineChart(dataList = dataList)
    }
}


@Composable
private fun KLineChart(dataList: List<KLinePriceData>) {
    if (dataList.isEmpty()) return
    var width = 0f
    var height = CHART_HEIGHT.value
    var downX = 0f
    // 在屏幕中的第一个蜡烛图对应集合的起始下标
    var indexStart = 0
    // 在屏幕中的第一个蜡烛图对应集合的结束下标
    var indexEnd by remember{ mutableStateOf(dataList.size - 1)}
    // 是否显示十字光标
    var isShowCross by remember{ mutableStateOf(false)}
    // 十字光标x轴坐标
    var crossX by remember { mutableStateOf(0f) }
    // 十字光标y轴坐标
    var crossY by remember { mutableStateOf(0f) }
    // 放大缩小比例
    var scale by remember{ mutableStateOf(SCALE_DEFAULT)}
    var yMaxValue = 0f
    var yMinValue = 0f
    var maxValue = 0f
    var minValue = 0f
    var twoPointsDis = 0f
    var yInterval = 0f
    var candleWidth = 0f
    var candleSpace = 0f
    var count = 0
    var yValueInterval = 0f

    val framePaint = Paint()
    framePaint.isAntiAlias = true
    framePaint.style = PaintingStyle.Stroke
    framePaint.strokeWidth = 1f
    framePaint.color = Gray_aaa

    val crossPaint = Paint()
    crossPaint.isAntiAlias = true
    crossPaint.style = PaintingStyle.Stroke
    crossPaint.strokeWidth = 2f
    crossPaint.color = Color.Black

    val yValuePaint = TextPaint()
    yValuePaint.isAntiAlias = true
    yValuePaint.textSize = LocalContext.current.resources.getDimension(R.dimen.dp_8)

    val candlePaint = Paint()
    candlePaint.isAntiAlias = true
    candlePaint.strokeWidth = 1f
    // 最大值，最小值，横线长度
    val lineWidth = 20.dp.value

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(CHART_HEIGHT)
        .padding(CHART_PADDING)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    while (true) {
                        val event: PointerEvent = awaitPointerEvent(PointerEventPass.Final)
                        if (event.changes.size == 1) {
                            // 1.单指操作
                            val pointer = event.changes[0]
                            if (!pointer.pressed) {
                                // 手指抬起,结束
                                break
                            } else {
                                if (pointer.previousPressed
                                    && abs(pointer.previousUptimeMillis - pointer.uptimeMillis)
                                    > viewConfiguration.longPressTimeoutMillis) {
                                    // 1.1长按
                                    isShowCross = true
                                    crossX = pointer.position.x
                                    crossY = pointer.position.y
                                } else if (isShowCross && pointer.previousPressed) {
                                    // 坐标显示并且上一次的手指是按压状态，可判断为长按后开始拖动的状态
                                    crossX = pointer.position.x
                                    crossY = pointer.position.y
                                } else if (pointer.previousPressed) {
                                    // 1.2没有进行长按的普通拖动
                                    val dx = pointer.position.x - downX
                                    count = (-dx / (candleWidth + candleSpace)).toInt()
                                    if (abs(count) >= 1) {
                                        indexStart += count
                                        indexEnd += count
                                        downX = pointer.position.x
                                        if (indexStart < 0) {
                                            indexEnd += abs(indexStart)
                                            indexStart = 0
                                        }
                                        if (indexEnd > dataList.size - 1) {
                                            indexStart += indexEnd - dataList.size
                                            indexEnd = dataList.size - 1
                                        }
                                    }
                                } else if (!pointer.previousPressed) {
                                    // 上一次手指没有按压，可以判断为单次点击事件
                                    downX = pointer.position.x
                                    if (isShowCross) {
                                        isShowCross = false
                                    }
                                }
                            }
                        } else if (event.changes.size > 1) {
                            // 2.多指操作
                            if (!event.changes[0].pressed || !event.changes[1].pressed) {
                                // 多指操作时，前两个主要手指抬起，可以判断为手势抬起
                                break
                            }
                            // 缩放处理
                            val dis = distance(event)
                            val minDis: Double = (width / 50.0).coerceAtLeast(4.dp.toPx().toDouble())
                            if (dis > minDis) {
                                if (dis > twoPointsDis) {
                                    twoPointsDis = dis
                                    // 放大
                                    scale += SCALE_STEP
                                } else if (dis < twoPointsDis) {
                                    twoPointsDis = dis
                                    // 缩小
                                    scale -= SCALE_STEP
                                }
                                if (scale > SCALE_MAX) {
                                    twoPointsDis = dis
                                    scale = SCALE_MAX
                                }
                                if (scale < SCALE_MIN) {
                                    twoPointsDis = dis
                                    scale = SCALE_MIN
                                }
                            }
                        }
                    }
                }
            }
        }
        .drawWithContent {
            drawContent()
            if (isShowCross) {
                drawIntoCanvas {
                    val priceStr = yToPrice(crossY, yMaxValue, yMinValue, height).toString()
                    val textWidth = yValuePaint.measureText(priceStr)
                    // 绘制十字光标
                    it.drawLine(Offset(0f, crossY), Offset(width - textWidth, crossY), crossPaint)
                    it.drawLine(Offset(crossX, 0f), Offset(crossX, height), crossPaint)
                    // 绘制交叉线上的价格
                    yValuePaint.color = Color.Blue.toArgb()
                    it.nativeCanvas.drawText(priceStr, width - textWidth, crossY, yValuePaint)
                }
            }
        }
    ) {
        width = drawContext.size.width
        height = drawContext.size.height
        // y轴等分高度
        yInterval = height / DIVIDER_NUM
        // 蜡烛宽度
        candleWidth = CANDLE_DEFAULT_WIDTH.toPx() * scale
        // 蜡烛间隙
        candleSpace = CANDLE_DEFAULT_SPACE_WIDTH.toPx() * scale
        // 当前画布能够放置蜡烛的数量
        count = (width / (candleSpace + candleWidth)).toInt()
        indexStart = indexEnd - count
        if (indexStart < 0) {
            // 边界值处理
            indexStart = 0
            indexEnd = count
        }
        // 计算当前画布中的最高股价和最低股价
        maxValue = dataList[indexStart].mMaxPrice
        minValue = dataList[indexStart].mMinPrice
        for (i in indexStart until indexEnd) {
            // 找出一屏幕内，股价的最大值和最小值
            if (dataList[i].mMaxPrice > maxValue) {
                maxValue = dataList[i].mMaxPrice
            }
            if (dataList[i].mMinPrice < minValue) {
                minValue = dataList[i].mMinPrice
            }
        }
        // y轴最大坐标，最小坐标 与最大价格/最小价格流出间距，用于给最大值和最小值流出绘制空间
        yMaxValue = maxValue + getOffset(maxValue)
        yMinValue = minValue - getOffset(minValue)
        // 股价等分间隔
        yValueInterval = (yMaxValue - yMinValue) / DIVIDER_NUM
        drawIntoCanvas {
            // 1.绘制边框及绘制y轴等分线
            it.drawRect(0f, 0f, width, height, framePaint)
            it.drawLine(Offset(0f, yInterval), Offset(width, yInterval), framePaint)
            it.drawLine(Offset(0f, yInterval * 2), Offset(width, yInterval * 2), framePaint)
            it.drawLine(Offset(0f, yInterval * 3), Offset(width, yInterval * 3), framePaint)
            // 2.绘制柱状图及上下阴线
            var startX = 0f
            for (i in indexStart until indexEnd) {
                if (dataList[i].mClosePrice  > dataList[i].mOpenPrice) {
                    candlePaint.color = Red_F54346
                    candlePaint.style = PaintingStyle.Stroke
                } else {
                    candlePaint.color = Green_14BB71
                    candlePaint.style = PaintingStyle.Fill
                }
                // 绘制矩形
                var offset = 0f
                if (dataList[i].mClosePrice == dataList[i].mOpenPrice) offset = 0.1f // 开盘价等收盘价，绘制一个0.1px的实线
                it.drawRect(startX, priceToY(dataList[i].mClosePrice + offset, yMaxValue, yMinValue, height),
                    startX + candleWidth,
                    priceToY(dataList[i].mOpenPrice, yMaxValue, yMinValue, height), candlePaint)
                // 绘制上阴线
                it.drawLine(
                    Offset(startX + candleWidth / 2,
                        priceToY(Math.max(dataList[i].mOpenPrice, dataList[i].mClosePrice), yMaxValue, yMinValue, height)),
                    Offset((startX + candleWidth / 2),
                        priceToY(dataList[i].mMaxPrice, yMaxValue, yMinValue, height)),
                    candlePaint)
                // 绘制下阴线
                it.drawLine(
                    Offset(startX + candleWidth / 2,
                        priceToY(Math.min(dataList[i].mOpenPrice, dataList[i].mClosePrice), yMaxValue, yMinValue, height)),
                    Offset((startX + candleWidth / 2),
                        priceToY(dataList[i].mMinPrice, yMaxValue, yMinValue, height)),
                    candlePaint)
                // 标示最大值和最小值
                if (dataList[i].mMaxPrice == maxValue) {
                    val maxValueLength = yValuePaint.measureText(maxValue.toString())
                    if (startX + (candleWidth / 2) + lineWidth + maxValueLength <= width) {
                        // 未超出边界，再进行绘制
                        candlePaint.color = Color.Black
                        it.drawLine(
                            Offset(startX + (candleWidth / 2),
                                priceToY(dataList[i].mMaxPrice, yMaxValue, yMinValue, height)),
                            Offset(startX + (candleWidth / 2) + lineWidth,
                                priceToY(dataList[i].mMaxPrice, yMaxValue, yMinValue, height)), candlePaint);
                        it.nativeCanvas.drawText(
                            numToString(maxValue), startX + (candleWidth / 2) + lineWidth,
                            priceToY(dataList[i].mMaxPrice, yMaxValue, yMinValue, height) + 3.dp.toPx(), yValuePaint)
                    }
                } else if (dataList[i].mMinPrice == minValue) {
                    val minValueLength = yValuePaint.measureText(minValue.toString())
                    if (startX + (candleWidth / 2) + lineWidth + minValueLength <= width) {
                        // 未超出边界，再进行绘制
                        candlePaint.color = Color.Black
                        it.drawLine(
                            Offset(startX + (candleWidth / 2), priceToY(dataList[i].mMinPrice, yMaxValue, yMinValue, height)),
                            Offset(startX + (candleWidth / 2) + lineWidth, priceToY(dataList[i].mMinPrice,yMaxValue, yMinValue, height)),
                            candlePaint)
                        it.nativeCanvas.drawText(
                            numToString(minValue), startX + (candleWidth / 2) + lineWidth,
                            priceToY(dataList[i].mMinPrice, yMaxValue, yMinValue, height), yValuePaint)
                    }
                }
                startX += candleWidth + candleSpace
            }
            // 3.绘制y轴坐标
            yValuePaint.color = Color.Black.toArgb()
            it.nativeCanvas.drawText(numToString(yMaxValue), 0f, yValuePaint.textSize, yValuePaint)
            it.nativeCanvas.drawText(numToString(yMaxValue - yValueInterval), 0f, yInterval + yValuePaint.textSize, yValuePaint)
            it.nativeCanvas.drawText(numToString(yMaxValue - yValueInterval * 2), 0f, yInterval * 2 + yValuePaint.textSize, yValuePaint)
            it.nativeCanvas.drawText(numToString(yMaxValue - yValueInterval * 3), 0f, yInterval * 3 + yValuePaint.textSize, yValuePaint)
            it.nativeCanvas.drawText(numToString(yMinValue), 0f, height, yValuePaint)
        }
    }
}


@Preview
@Composable
fun ShowChartView() {
    KLineChart(generateKLineData(LocalContext.current))
}







