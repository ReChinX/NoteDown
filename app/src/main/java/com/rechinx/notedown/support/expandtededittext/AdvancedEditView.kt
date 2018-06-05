package com.rechinx.notedown.support.expandtededittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent
import com.rechinx.notedown.R
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.EditorInfo


class AdvancedEditView : AppCompatEditText {

    private val mPaint: Paint = Paint()

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): super(context, attrs, defStyleAttr) {
        mPaint.color = resources.getColor(R.color.text_line_divider)
        mPaint.strokeWidth = 1.0f
    }

    override fun onDraw(canvas: Canvas) {
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

        super.onDraw(canvas)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return DeleteInputConnection(super.onCreateInputConnection(outAttrs),
                true)
    }

    private inner class DeleteInputConnection(target: InputConnection, mutable: Boolean) : InputConnectionWrapper(target, mutable) {

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            return if (beforeLength == 1 && afterLength == 0) {
                sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL)) && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_DEL))
            } else super.deleteSurroundingText(beforeLength, afterLength)

        }

    }

}
