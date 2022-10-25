package com.monkey.paintertest

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.monkey.paintertest.databinding.ActivityMainBinding
import java.util.ArrayList

class MainActivity: Activity() {
    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        val sizeClickListener = View.OnClickListener {
            mBinding.paint.mBrushSize = when (it) {
                mBinding.btnSize1 -> 1
                mBinding.btnSize2 -> 2
                mBinding.btnSize3 -> 3
                else -> 1
            }
            Toast.makeText(this@MainActivity, "brushSize : " + mBinding.paint.mBrushSize, Toast.LENGTH_SHORT).show()
        }

        mBinding.btnSize1.setOnClickListener(sizeClickListener)
        mBinding.btnSize2.setOnClickListener(sizeClickListener)
        mBinding.btnSize3.setOnClickListener(sizeClickListener)

        val colorClickListener = View.OnClickListener {
            mBinding.paint.mBrushColor = when(it) {
                mBinding.btnColorRed -> Color.RED
                mBinding.btnColorGreen -> Color.GREEN
                mBinding.btnColorBlue -> Color.BLUE
                mBinding.btnColorClear -> Color.TRANSPARENT
                else -> Color.BLACK
            }

            val colorCode = when(it) {
                mBinding.btnColorRed -> "RED"
                mBinding.btnColorGreen -> "GREEN"
                mBinding.btnColorBlue -> "BLUE"
                mBinding.btnColorClear -> "TRANSPARENT"
                else -> "BLACK"
            }

            mBinding.paint.isEraser = it == mBinding.btnColorClear
            Toast.makeText(this@MainActivity, "color : $colorCode", Toast.LENGTH_SHORT).show()
        }
        mBinding.btnColorRed.setOnClickListener(colorClickListener)
        mBinding.btnColorGreen.setOnClickListener(colorClickListener)
        mBinding.btnColorBlue.setOnClickListener(colorClickListener)
        mBinding.btnColorClear.setOnClickListener(colorClickListener)

        mBinding.btnUndo.setOnClickListener { mBinding.paint.undo() }
        mBinding.btnRedo.setOnClickListener { mBinding.paint.redo() }

        mBinding.btnClearAll.setOnClickListener {
            mBinding.paint.clearAll()
        }

        mBinding.paint.invalidateListener = Runnable {
            mBinding.btnUndo.isEnabled = mBinding.paint.isEnableUndo()
            mBinding.btnRedo.isEnabled = mBinding.paint.isEnableRedo()
        }

        mBinding.btnMode.setOnClickListener {
            mBinding.paint.switchMode()
            Toast.makeText(this@MainActivity, "Mode : " + if(mBinding.paint.isDrawingMode) "DRAWING" else "SCROLL", Toast.LENGTH_SHORT).show()
        }

        mBinding.btnShowBackground.setOnClickListener {
            Toast.makeText(this@MainActivity, "Background visibility : " + if(mBinding.paint.showBackground) "VISIBLE" else "GONE", Toast.LENGTH_SHORT).show()
            mBinding.paint.switchBackground()
        }
    }
}