package com.monkey.paintertest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.ArrayList

class PaintView : View {
    private var mPaint: Paint?
    private var drawPoints = ArrayList<DrawPoint>()
    private var lastPoints = ArrayList<DrawPoint>()

    private val lastHistory = DrawPointHistory()
    private val undoHistory = DrawPointHistory()

    var mBrushSize = 1
    var mBrushColor:Int = Color.BLACK

    var isEraser:Boolean = false
    var isDrawingMode: Boolean = true
    var showBackground: Boolean = false

    lateinit var invalidateListener: Runnable

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
        mPaint = getNewPaint()

        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun undo() {
        undoOrReDo(true)
    }

    fun redo() {
        undoOrReDo(false)
    }

    private fun undoOrReDo(isUndo: Boolean) {
        val loadTargetHistory = if(isUndo) lastHistory else undoHistory
        var rewriteTargetHistory = if(isUndo) undoHistory else lastHistory

        if(!loadTargetHistory.isEmpty()) {
            val historyPoints = loadTargetHistory.popLastHistory()

            if(isUndo) {
                drawPoints.removeAll(historyPoints)
            }else {
                drawPoints.addAll(historyPoints)
            }

            val rewritePoints = ArrayList<PaintView.DrawPoint>()
            rewritePoints.addAll(historyPoints)
            rewriteTargetHistory.pushNewHistory(rewritePoints)

            invalidate()
        }
    }

    fun isEnableUndo(): Boolean {
        return !lastHistory.isEmpty()
    }

    fun isEnableRedo(): Boolean {
        return !undoHistory.isEmpty()
    }

    fun clearAll() {
        drawPoints.clear()
        lastHistory.clear()
        undoHistory.clear()
        invalidate()
    }
    private fun getNewPaint() : Paint {
        val mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isDither = true
        mPaint.strokeWidth = 15f * mBrushSize
        mPaint.color = mBrushColor
        if(isEraser) {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }else{
            mPaint.xfermode = null
        }
        return mPaint
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(showBackground) {
            canvas?.drawColor(Color.WHITE)
        }else{
            canvas?.drawColor(Color.TRANSPARENT)
        }

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

    fun switchMode() {
        isDrawingMode = !isDrawingMode
    }

    fun switchBackground() {
        showBackground = !showBackground
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(isDrawingMode) {
            val action = event.action
            val x = event.x
            val y = event.y

            if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                if(action == MotionEvent.ACTION_DOWN) {
                    mPaint = getNewPaint()
                    lastPoints = ArrayList<DrawPoint>()
                    undoHistory.clear()
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
        }

        return isDrawingMode
    }

    override fun invalidate() {
        super.invalidate()
        invalidateListener.run()
    }

    internal class DrawPoint(var x: Float, var y: Float, var isDraw: Boolean, var paint: Paint)

    internal class DrawPointHistory() {
        private val points = Stack<ArrayList<DrawPoint>>(10)

        fun pushNewHistory(pointList: ArrayList<DrawPoint>) {
            points.pushNewThing(pointList)
        }

        fun popLastHistory(): ArrayList<DrawPoint> {
            return points.popLastThing()
        }

        fun isEmpty(): Boolean {
            return points.isEmpty()
        }

        fun clear() {
            points.clear()
        }
    }
}