package com.example.composestudy.snowball.stockchart.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.GestureDetector
import com.example.composestudy.R
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.composestudy.snowball.stockchart.data.KLinePriceData
import com.example.composestudy.snowball.stockchart.utils.numToString
import com.example.composestudy.snowball.stockchart.utils.priceToY
import com.example.composestudy.snowball.stockchart.utils.yToPrice
import java.util.ArrayList
import kotlin.math.sqrt

/**
 * @author jingqingqing
 * @date 2021/8/21
 */
class AndroidViewKLineView : View, GestureDetector.OnGestureListener {
    companion object {
        private const val DEFAULT_SHOW_CANDLE_NUM = 50
        private const val DEFAULT_INTERVAL = 4
        private const val DEFAULT_MAX_SACLE = 10
        private const val DEFAULT_SACLE = 5
    }

    private var mCandlePaint: Paint = Paint()
    private var mFramePaint: Paint = Paint()
    private var mYValuePaint: Paint = TextPaint()
    private var mCrossPaint: Paint = Paint()
    private var dataList: List<KLinePriceData> = ArrayList()
    private var mHeight = 0f
    private var mWidth = 0f
    private var mLeft = 0
    private var candleWidth = 0f
    private var candleSpace = 0f
    private var indexStart = 0
    private var indexEnd = 0
    private var mYInterval = 0f
    private var maxValue = 0f
    private var minValue = 0f
    private var yMaxValue = 0f
    private var yMinValue = 0f
    private var downX = 0f
    private var isShowCross = false
    private var crossX = 0f
    private var crossY = 0f
    private var gestureDetector: GestureDetector? = null
    private var twoPointsDis = 0f
    // 放大缩小比例
    private var scale = DEFAULT_SACLE
    private var maxScaleDis = 0f
    private var yOffset = 0
    private var lineWidth = 0
    private var minDistance = 0

    constructor(context: Context?): this(context, null)
    constructor(context: Context?, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initParams()
    }


    private fun initParams() {
        mCandlePaint.isAntiAlias = true
        mCandlePaint.style = Paint.Style.STROKE
        mCandlePaint.strokeWidth = 2f
        mCandlePaint.color = resources.getColor(R.color.red)

        mFramePaint.isAntiAlias = true
        mFramePaint.style = Paint.Style.STROKE
        mFramePaint.strokeWidth = 1f
        mFramePaint.color = resources.getColor(R.color.color_aaa)

        mYValuePaint.isAntiAlias = true
        mYValuePaint.textSize = resources.getDimension(R.dimen.dp_6)
        mYValuePaint.color = resources.getColor(R.color.black)

        mCrossPaint.isAntiAlias = true
        mCrossPaint.style = Paint.Style.FILL
        mCrossPaint.color = resources.getColor(R.color.color_aaa)

        //手势监听
        gestureDetector = GestureDetector(context, this)
        yOffset = context.resources.getDimensionPixelOffset(R.dimen.dp_4)
        lineWidth = context.resources.getDimensionPixelOffset(R.dimen.dp_10)
        minDistance = context.resources.getDimensionPixelOffset(R.dimen.dp_4)
    }

    fun initStockData(list: List<KLinePriceData>) {
        dataList = list
        // 给定一个默认其实位置
        indexStart = dataList.size - DEFAULT_SHOW_CANDLE_NUM
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mHeight = (bottom - top).toFloat()
        mWidth = (right - left).toFloat()
        mLeft = left
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initCandleData()
        // 1.绘制边框，固定不变
        drawFrame(canvas)
        // 2.绘制y轴坐标
        drawYValue(canvas)
        //        // 3.绘制蜡烛图
        drawCandles(canvas)
        if (isShowCross) {
            // 4.绘制十字交叉线
            drawCross(canvas)
        }
        Log.e("jingqingqing", "end time:" + System.currentTimeMillis())
    }

    private fun initCandleData() {
        candleWidth = context.resources.getDimension(R.dimen.dp_2) * scale
        candleSpace = context.resources.getDimension(R.dimen.dp_1) * scale
        val count = (mWidth / (candleSpace + candleWidth)).toInt()
        indexEnd = indexStart + count - 1
        if (indexEnd > dataList.size - 1) {
            indexEnd = dataList.size - 1
        }
    }

    private fun drawFrame(canvas: Canvas) {
        canvas.drawRect(0f, 0f, mWidth, mHeight, mFramePaint)
        mYInterval = mHeight / DEFAULT_INTERVAL
        mFramePaint.strokeWidth = 0.5f
        canvas.drawLine(0f, mYInterval, mWidth, mYInterval, mFramePaint)
        canvas.drawLine(0f, mYInterval * 2, mWidth, mYInterval * 2, mFramePaint)
        canvas.drawLine(0f, mYInterval * 3, mWidth, mYInterval * 3, mFramePaint)
    }

    private fun drawYValue(canvas: Canvas) {
        maxValue = dataList[indexStart].mMaxPrice
        minValue = dataList[indexStart].mMinPrice
        for (i in indexStart..indexEnd) {
            if (dataList[i].mMaxPrice > maxValue) {
                maxValue = dataList[i].mMaxPrice
            }
            if (dataList[i].mMinPrice < minValue) {
                minValue = dataList[i].mMinPrice
            }
        }
        yMaxValue = maxValue + yOffset
        yMinValue = minValue - yOffset
        // 四等分
        val interval = (yMaxValue - yMinValue) / DEFAULT_INTERVAL
        val width = mYValuePaint.measureText(yMinValue.toString())
        // 绘制y轴坐标
        canvas.drawText(yMaxValue.toString(), 0f, width / 2, mYValuePaint)
        canvas.drawText(
            (yMaxValue - interval).toString(),
            0f,
            mYInterval + width / 2,
            mYValuePaint
        )
        canvas.drawText(
            (yMaxValue - interval * 2).toString(),
            0f,
            mYInterval * 2 + width / 2,
            mYValuePaint
        )
        canvas.drawText(
            (yMaxValue - interval * 3).toString(),
            0f,
            mYInterval * 3 + width / 2,
            mYValuePaint
        )
        canvas.drawText(yMinValue.toString(), 0f, mHeight - yOffset / 2f, mYValuePaint)
    }

    private fun drawCandles(canvas: Canvas) {
        var startX = mLeft.toFloat()
        for (i in indexStart..indexEnd) {
            if (dataList[i].mClosePrice > dataList[i].mOpenPrice) {
                mCandlePaint.color = resources.getColor(R.color.red)
                mCandlePaint.style = Paint.Style.STROKE
            } else {
                mCandlePaint.color = resources.getColor(R.color.green)
                mCandlePaint.style = Paint.Style.FILL
            }
            // 绘制矩形
            canvas.drawRect(
                startX, priceToY(dataList[i].mClosePrice), startX + candleWidth, priceToY(
                    dataList[i].mOpenPrice
                ), mCandlePaint
            )
            // 绘制上阴线
            canvas.drawLine(
                startX + candleWidth / 2,
                priceToY(Math.max(dataList[i].mOpenPrice, dataList[i].mClosePrice)),
                startX + candleWidth / 2,
                priceToY(dataList[i].mMaxPrice), mCandlePaint
            )
            // 绘制下阴线
            canvas.drawLine(
                startX + candleWidth / 2,
                priceToY(Math.min(dataList[i].mOpenPrice, dataList[i].mClosePrice)),
                startX + candleWidth / 2,
                priceToY(dataList[i].mMinPrice), mCandlePaint
            )
            // 标示最大值和最小值
            if (dataList[i].mMaxPrice == maxValue) {
                mCandlePaint.color = resources.getColor(R.color.black)
                canvas.drawLine(
                    startX + candleWidth / 2,
                    priceToY(dataList[i].mMaxPrice),
                    startX + candleWidth / 2 + lineWidth,
                    priceToY(dataList[i].mMaxPrice), mCandlePaint!!
                )
                canvas.drawText(
                    maxValue.toString(), startX + candleWidth / 2 + lineWidth, priceToY(
                        dataList[i].mMaxPrice
                    ), mYValuePaint
                )
            } else if (dataList[i].mMinPrice == minValue) {
                mCandlePaint.color = resources.getColor(R.color.black)
                canvas.drawLine(
                    startX + candleWidth / 2,
                    priceToY(dataList[i].mMinPrice),
                    startX + candleWidth / 2 + lineWidth,
                    priceToY(dataList[i].mMinPrice),
                    mCandlePaint
                )
                canvas.drawText(
                    minValue.toString(), startX + candleWidth / 2 + lineWidth,
                    priceToY(dataList[i].mMinPrice), mYValuePaint
                )
            }
            startX += candleWidth + candleSpace
        }
    }

    private fun drawCross(canvas: Canvas) {
        canvas.drawLine(0f, crossY, mWidth, crossY, mYValuePaint)
        canvas.drawLine(crossX, 0f, crossX, mHeight, mYValuePaint)
        val width = mYValuePaint.measureText(numToString(yToPrice(crossY)))
        canvas.drawRect(0f, crossY - lineWidth, width, crossY + lineWidth, mCrossPaint)
        canvas.drawText(numToString(yToPrice(crossY)), 0f, crossY + yOffset / 2f, mYValuePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        maxScaleDis = mWidth
        gestureDetector!!.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> downX = event.x
            MotionEvent.ACTION_MOVE -> {
                if (isShowCross) {
                    crossX = event.x
                    crossY = event.y
                } else if (event.pointerCount == 1) {
                    // k线滑动
                    val dx = event.x - downX
                    val count = (-dx / (candleWidth + candleSpace * 2)).toInt()
                    if (Math.abs(count) >= 1) {
                        indexStart += count
                        indexEnd += count
                        downX = event.x
                        if (indexStart < 0) {
                            indexStart = 0
                            indexEnd = DEFAULT_SHOW_CANDLE_NUM
                        }
                        if (indexEnd > dataList.size - 1) {
                            indexEnd = dataList.size - 1
                            indexStart = indexEnd - DEFAULT_SHOW_CANDLE_NUM
                        }
                    }
                } else if (event.pointerCount >= 2) {
                    // 缩放处理
                    val dis = distance(event)
                    val minDis = Math.max(mWidth / DEFAULT_SHOW_CANDLE_NUM, minDistance.toFloat())
                    if (dis > minDis) {
                        val percent = dis / maxScaleDis
                        if (dis > twoPointsDis) {
                            // 放大
                            scale++
                        } else if (dis < twoPointsDis) {
                            // 缩小
                            scale--
                        }
                        if (scale > DEFAULT_MAX_SACLE) {
                            scale = DEFAULT_MAX_SACLE
                        }
                        if (scale < 1) {
                            scale = 1
                        }
                        twoPointsDis = dis
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    private fun priceToY(f: Float): Float {
        return priceToY(f = f, yMaxValue = yMaxValue, yMinValue = yMinValue, height = mHeight)
//        return ((yMaxValue - f) / (yMaxValue - yMinValue) * mHeight)
    }

    private fun yToPrice(f: Float): Float {
        return yToPrice(yMaxValue = yMaxValue, yMinValue = yMinValue, f = f, height = mHeight)
    }

//    private fun numToString(f: Float): String {
//        val format = DecimalFormat("0.00")
//        return format.format(f.toDouble())
//    }

    // 计算移动距离
    private fun distance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        if (isShowCross) {
            isShowCross = false
            invalidate()
        }
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        if (!isShowCross) {
            isShowCross = true
            crossX = e.x
            crossY = e.y
            invalidate()
        }
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}