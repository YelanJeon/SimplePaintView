package com.monkey.paintertest

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
            mBinding.paint.isEreaser = it == mBinding.btnColorClear
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
    }
}