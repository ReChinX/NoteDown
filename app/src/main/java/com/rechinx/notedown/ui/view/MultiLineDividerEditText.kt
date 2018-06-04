package com.rechinx.notedown.ui.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity

import com.rechinx.notedown.R
import com.scrat.app.richtext.RichEditText

class MultiLineDividerEditText : RichEditText {

    private var ITEM_HEIGHT = 125f
    internal var reLayout = false
    internal var textWatcher: TextWatcher? = null

    private val mPaint: Paint

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): super(context, attrs, defStyleAttr) {
//        ITEM_HEIGHT = resources.getDimension(R.dimen.note_detail_item_height).toInt().toFloat()
//        addTextChangedListener(object : android.text.TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                if (textWatcher != null) {
//                    textWatcher!!.beforeTextChanged(charSequence, i, i1, i2)
//                }
//            }
//
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                val add = ITEM_HEIGHT
//
//                setLineSpacing(0f, 1f)
//                setLineSpacing(add, 0f)
//                includeFontPadding = false
//                val top = ((add - textSize) * 0.5f).toInt()
//                setPadding(paddingLeft, top, paddingRight, -top)
//                if (textWatcher != null) {
//                    textWatcher!!.onTextChanged(charSequence, i, i1, i2)
//                }
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//                if (textWatcher != null) {
//                    textWatcher!!.afterTextChanged(editable)
//                }
//            }
//        })
        mPaint = Paint()
        mPaint.color = resources.getColor(R.color.text_line_divider)
        mPaint.strokeWidth = 1.0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw divider
        val etHeight = height
        val etWidth = width
        val lHeight = lineHeight
        var baseline = baseline
        var lineCnt = lineCount
        val pageLineCnt = etHeight / lHeight
        val offset = lHeight / 3

        if (lineCnt < pageLineCnt) {
            lineCnt = pageLineCnt
        }

        for (i in 0 until lineCnt) {
            //canvas.drawLine(0f, (baseline + offset).toFloat(), etWidth.toFloat(), (baseline + offset).toFloat(), mPaint)
            baseline += lHeight

        }

//        if (!reLayout) {
//            reLayout = true
//            val add = ITEM_HEIGHT
//            includeFontPadding = false
//            setLineSpacing(add, 0f)
//            val top = ((add - textSize) * 0.5f).toInt()
//            setPadding(paddingLeft, top, paddingRight, -top)
//            requestLayout()
//            invalidate()
//        }
    }

    fun addTextWatcher(textWatcher: TextWatcher) {
        this.textWatcher = textWatcher
    }

    interface TextWatcher {
        fun beforeTextChanged(var1: CharSequence, var2: Int, var3: Int, var4: Int)

        fun onTextChanged(var1: CharSequence, var2: Int, var3: Int, var4: Int)

        fun afterTextChanged(var1: Editable)
    }
}
