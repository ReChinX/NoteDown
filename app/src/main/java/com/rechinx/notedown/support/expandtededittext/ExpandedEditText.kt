package com.rechinx.notedown.support.expandtededittext

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.rechinx.notedown.R
import android.view.KeyEvent.KEYCODE_DEL
import android.text.Selection.getSelectionStart
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.support.glide.GlideApp
import com.rechinx.notedown.support.glide.GlideRequests
import com.rechinx.notedown.utils.ScreenUtils
import java.util.regex.Pattern
import android.widget.EditText



class ExpandedEditText: ScrollView {

    private val TAG = this.javaClass.simpleName

    private val EDIT_PADDING = 10
    private val EDIT_FIRST_PADDING_TOP = 10

    private var viewTagIndex = 1
    private var container: LinearLayout
    private var inflater: LayoutInflater
    private lateinit var lastFocusEditText: EditText
    private var glideRequests: GlideRequests
    private var mTransitioner: LayoutTransition
    private var keyListener: OnKeyListener
    private var btnClickLisener: OnClickListener
    private var focusLisener: OnFocusChangeListener
    private var disappearingImageIndex: Int = 0
    private var editNormalPadding: Int = 0

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):super(context, attrs, defStyleAttr) {
        // gets globals
        inflater = LayoutInflater.from(context)
        glideRequests = GlideApp.with(context)

        // init container
        container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        addView(container, layoutParams)

        // init variables
        editNormalPadding = ScreenUtils.dpToPx(EDIT_PADDING.toFloat()).toInt()

        // register listener
        keyListener = OnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_DOWN && keyCode === KeyEvent.KEYCODE_DEL) {
                val edit = v as EditText
                onBackspacePress(edit)
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "now backspace pressed")
                }
            }
            false
        }

        btnClickLisener = OnClickListener { v ->
            val parentView = v.parent as RelativeLayout
            onImageCloseClick(parentView)
        }

        focusLisener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lastFocusEditText = v as EditText
            }
        }

        // configure transition
        mTransitioner = LayoutTransition()
        container.layoutTransition = mTransitioner
        mTransitioner.addTransitionListener(object : LayoutTransition.TransitionListener {

            override fun startTransition(transition: LayoutTransition,
                                         container: ViewGroup, view: View, transitionType: Int) {

            }

            override fun endTransition(transition: LayoutTransition,
                                       container: ViewGroup, view: View, transitionType: Int) {
                if (!transition.isRunning && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                    mergeEditText();
                }
            }
        })
        mTransitioner.setDuration(300)
    }

    private fun mergeEditText() {
        val preView = container.getChildAt(disappearingImageIndex - 1)
        val nextView = container.getChildAt(disappearingImageIndex)
        if (preView != null && preView is EditText && null != nextView
                && nextView is EditText) {
            val preEdit = preView as EditText
            val nextEdit = nextView as EditText
            val str1 = preEdit.text.toString()
            val str2 = nextEdit.text.toString()
            var mergeText = ""
            if (str2.isNotEmpty()) {
                mergeText = str1 + "\n" + str2
            } else {
                mergeText = str1
            }

            container.layoutTransition = null
            container.removeView(nextEdit)
            preEdit.setText(mergeText)
            preEdit.requestFocus()
            preEdit.setSelection(str1.length, str1.length)
            container.layoutTransition = mTransitioner

        }
    }
    private fun onBackspacePress(edit: EditText) {
        val startSelection = edit.selectionStart
        if (startSelection == 0) {
            val editIndex = container.indexOfChild(edit)
            val preView = container.getChildAt(editIndex - 1)
            if (null != preView) {
                if (preView is RelativeLayout) {
                    onImageCloseClick(preView)
                } else if (preView is EditText) {
                    val str1 = edit.text.toString()
                    val preEdit = preView as EditText
                    val str2 = preEdit.text.toString()

                    container.layoutTransition = null
                    container.removeView(edit)
                    container.layoutTransition = mTransitioner

                    preEdit.setText(str2 + str1)
                    preEdit.requestFocus()
                    preEdit.setSelection(str2.length, str2.length)
                    lastFocusEditText = preEdit
                }
            }
        }
    }

    private fun onImageCloseClick(preView: RelativeLayout) {
        if(!mTransitioner.isRunning) {
            disappearingImageIndex = container.indexOfChild(preView)
            container.removeView(preView)
        }
    }


    private fun createEditText(paddingTop: Int): EditText {
        val editText = inflater.inflate(R.layout.edit_item, null) as EditText
        editText.setOnKeyListener(keyListener)
        editText.onFocusChangeListener = focusLisener
        editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop)
        editText.tag = viewTagIndex++
        return editText
    }

    private fun createImageLayout(): RelativeLayout {
        val layout = inflater.inflate(R.layout.edit_image, null) as RelativeLayout
        layout.setPadding(editNormalPadding, editNormalPadding, editNormalPadding, editNormalPadding)
        layout.tag = viewTagIndex++
        val closeView = layout.findViewById<View>(R.id.image_close)
        closeView.tag = layout.tag
        closeView.setOnClickListener(btnClickLisener)
        return layout
    }

    fun insertImage(uri: Uri) {
        val lastEditStr = lastFocusEditText.text.toString()
        val cursorIndex = lastFocusEditText.selectionStart
        val editTextStr = lastEditStr.substring(0, cursorIndex).trim()
        val lastEditIndex = container.indexOfChild(lastFocusEditText)

        if(lastEditStr.isEmpty() || editTextStr.isEmpty()) {
            addImageViewAtIndex(lastEditIndex, uri)
        }else {
            lastFocusEditText.setText(editTextStr)
            val editTextStr2 = lastEditStr.substring(cursorIndex).trim()
            if(container.childCount == lastEditIndex + 1 || !editTextStr2.isEmpty()) {
                addEditTextAtIndex(lastEditIndex + 1, editTextStr2)
            }
            addImageViewAtIndex(lastEditIndex + 1, uri)
            lastFocusEditText.requestFocus()
            lastFocusEditText.setSelection(editTextStr.length, editTextStr.length)
        }
        hideKeyBoard()
    }

    fun setCursorVisible(flag: Boolean) {
        lastFocusEditText.isCursorVisible = flag
    }

    fun fromHtml(content: String?) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "note from editfragment is $content")
        }
        if(TextUtils.isEmpty(content)) {
            addEditText("")
            return
        }
        val pattern = Pattern.compile("<img src=\".*?\"/>")
        var tmp = content
        var flag = false
        while(true) {
            val localMatcher = pattern.matcher(tmp)
            if(!localMatcher.find()) break
            val st = localMatcher.start()
            val ed = localMatcher.end()

            if(BuildConfig.DEBUG) {
                Log.d(TAG, "start is $st and end is $ed")
            }

            if(st > 0) {
                val textStr = tmp!!.substring(0, st)
                if(!flag) {
                    addEditText(textStr)
                    flag = true
                }else {
                    lastFocusEditText.setText(textStr)
                }
            }

            var imgStr = tmp!!.substring(st, ed)
            val uriIndexStart = imgStr.indexOf("src=")
            val uriIndexEnd = imgStr.indexOf("/>")
            val uriStr = imgStr.substring(uriIndexStart + 5, uriIndexEnd - 1)
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "There is parser: $imgStr")
            }
            val relativeLayout = createImageLayout()
            val image = relativeLayout.findViewById<DataImageView>(R.id.edit_imageView)
            image.uri = Uri.parse(uriStr)
            glideRequests.asBitmap()
                    .load(Uri.parse(uriStr))
                    .centerCrop()
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val imageHeight = width * resource.height / resource.width
                            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight)
                            image.setImageBitmap(resource)
                            image.layoutParams = layoutParams
                        }
                    })
            container.addView(relativeLayout)
            addEditText("")
            tmp = tmp.substring(ed)
        }
        if(!TextUtils.isEmpty(tmp)) {
            if(!flag) {
                addEditText(tmp!!)
            }else {
                lastFocusEditText.setText(tmp!!)
            }
        }
    }

    private fun addEditText(text: String) {
        val editParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val editText = createEditText(ScreenUtils.dpToPx(EDIT_FIRST_PADDING_TOP.toFloat()).toInt())
        editText.setText(text)
        container.addView(editText, editParams)
        lastFocusEditText = editText
    }

    fun toHtml(): String {
        var ret = ""
        for(i in 0 until container.childCount) {
            val itemView = container.getChildAt(i)
            if(itemView is EditText) {
                ret += itemView.text.toString()
            }else if(itemView is RelativeLayout) {
                val image = itemView.findViewById<DataImageView>(R.id.edit_imageView)
                ret += "<img src=\"${image.uri.toString()}\"/>"
            }
        }
        return ret
    }

    private fun hideKeyBoard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm!!.hideSoftInputFromWindow(lastFocusEditText.windowToken, 0)
    }

    private fun addEditTextAtIndex(lastEditIndex: Int, editTextStr: String) {
        val editText = createEditText(ScreenUtils.dpToPx(EDIT_FIRST_PADDING_TOP.toFloat()).toInt())
        editText.setText(editTextStr)

        // edittext without transition
        container.layoutTransition = null
        container.addView(editText, lastEditIndex)
        container.layoutTransition = mTransitioner
    }

    private fun addImageViewAtIndex(lastEditIndex: Int, uri: Uri) {
        val relativeLayout = createImageLayout()
        val image = relativeLayout.findViewById<DataImageView>(R.id.edit_imageView)
        image.uri = uri
        glideRequests.asBitmap()
                .load(uri)
                .centerCrop()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val imageHeight = width * resource.height / resource.width
                        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight)
                        image.setImageBitmap(resource)
                        image.layoutParams = layoutParams
                    }
                })
        container.postDelayed({ container.addView(relativeLayout, lastEditIndex) }, 200)
    }

    fun getTitle(): String {
        var ret = ""
        for(i in 0 until container.childCount) {
            val itemView = container.getChildAt(i)
            if(itemView is EditText) {
                ret = itemView.text.toString()
                break
            }
        }
        return ret
    }

}