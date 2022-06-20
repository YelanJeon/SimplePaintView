package com.monkey.paintertest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isEmpty
import com.monkey.paintertest.databinding.PainterViewBinding
import java.util.ArrayList

class PainterView : ConstraintLayout {
    private val mBinding: PainterViewBinding by lazy {
        PainterViewBinding.bind(inflate(context, R.layout.painter_view, this))
    }
    private var mPaint: Paint?

    private var drawPoints = ArrayList<DrawPoint>()
    private var lastPoints = ArrayList<DrawPoint>()

    private val lastHistory = DrawPointHistory()
    private val undoHistory = DrawPointHistory()

    var mBrushSize = 1
    var mBrushColor:Int = Color.BLACK
    var isEreaser:Boolean = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        val sizeClickListener = OnClickListener {
            mBrushSize = when(it) {
                mBinding.btnSize1 -> 1
                mBinding.btnSize2 -> 2
                mBinding.btnSize3 -> 3
                else -> 1
            }
        }

        mBinding.btnSize1.setOnClickListener(sizeClickListener)
        mBinding.btnSize2.setOnClickListener(sizeClickListener)
        mBinding.btnSize3.setOnClickListener(sizeClickListener)

        val colorClickListener = OnClickListener {
            mBrushColor = when(it) {
                mBinding.btnColorRed -> Color.RED
                mBinding.btnColorGreen -> Color.GREEN
                mBinding.btnColorBlue -> Color.BLUE
                mBinding.btnColorClear -> Color.TRANSPARENT
                else -> Color.BLACK
            }
            isEreaser = it == mBinding.btnColorClear
        }
        mBinding.btnColorRed.setOnClickListener(colorClickListener)
        mBinding.btnColorGreen.setOnClickListener(colorClickListener)
        mBinding.btnColorBlue.setOnClickListener(colorClickListener)
        mBinding.btnColorClear.setOnClickListener(colorClickListener)

        val historyClickListener = OnClickListener {
            val isUndo = it == mBinding.btnUndo
            val loadTargetHistory = if(isUndo) lastHistory else undoHistory
            var rewriteTargetHistory = if(isUndo) undoHistory else lastHistory

            if(!loadTargetHistory.isEmpty()) {
                val historyPoints = loadTargetHistory.popLastHistory()
                if(isUndo) {
                    drawPoints.removeAll(historyPoints)
                }else {
                    drawPoints.addAll(historyPoints)
                }

                val rewritePoints = ArrayList<DrawPoint>()
                rewritePoints.addAll(historyPoints)
                rewriteTargetHistory.pushNewHistory(rewritePoints)

                invalidate()
            }
        }


        mBinding.btnUndo.setOnClickListener(historyClickListener)
        mBinding.btnRedo.setOnClickListener(historyClickListener)

        mBinding.btnClearAll.setOnClickListener {
            drawPoints.clear()
        }

        mPaint = getNewPaint()

        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun getNewPaint() : Paint {
        val mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isDither = true
        mPaint.strokeWidth = 15f * mBrushSize
        mPaint.color = mBrushColor
        if(isEreaser) {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }else{
            mPaint.xfermode = null
        }
        return mPaint
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (i in drawPoints.indices) {
            if (drawPoints[i].isDraw) {
                canvas!!.drawLine(
                    drawPoints[i - 1].x,
                    drawPoints[i - 1].y,
                    drawPoints[i].x,
                    drawPoints[i].y,
                    drawPoints[i].paint
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x
        val y = event.y

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
            if(action == MotionEvent.ACTION_DOWN) {
                mPaint = getNewPaint()
                lastPoints = ArrayList<DrawPoint>()
            }

            val drawPoint = DrawPoint(x, y, action != MotionEvent.ACTION_DOWN, mPaint!!);
            drawPoints.add(drawPoint)
            lastPoints.add(drawPoint)

            if(action == MotionEvent.ACTION_UP) {
                mPaint = null
                lastHistory.pushNewHistory(lastPoints)
            }

            invalidate()
        }
        return true
    }

    override fun invalidate() {
        super.invalidate()
        mBinding.btnUndo.isEnabled = !lastHistory.isEmpty()
        mBinding.btnRedo.isEnabled = !undoHistory.isEmpty()
    }

    internal class DrawPoint(var x: Float, var y: Float, var isDraw: Boolean, var paint: Paint)

    internal class DrawPointHistory() {
        private val MAX_HISTORY_COUNT = 10;
        private val points = ArrayList<ArrayList<DrawPoint>>()

        fun pushNewHistory(pointList: ArrayList<DrawPoint>) {
            points.add(pointList)
            if(points.size > MAX_HISTORY_COUNT) {
                points.removeAt(0)
            }
        }

        fun popLastHistory(): ArrayList<DrawPoint> {
            val returnHistory = points[points.size-1]
            points.remove(returnHistory)
            return returnHistory
        }

        fun isEmpty(): Boolean {
            return points.isEmpty()
        }
    }
}