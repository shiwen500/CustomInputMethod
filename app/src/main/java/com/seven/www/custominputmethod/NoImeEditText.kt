package com.seven.www.custominputmethod

import android.content.Context
import android.graphics.Rect
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * This EditText will suppresses IME show up
 */
class NoImeEditText : EditText {

    private val imm: InputMethodManager?
        get() = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        inputType = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        // We should hide ime if need.
        hideImIfNeed()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val ret = super.onTouchEvent(event)
        // Must be done after super.onTouchEvent()
        hideImIfNeed()
        return ret
    }

    private fun hideImIfNeed() {
        val imm = imm
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(applicationWindowToken, 0)
        }
    }
}
