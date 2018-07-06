package com.seven.www.custominputmethod

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button

/*
 * Button should listen to press event
 */
class CustomKeyButton : Button {

    private var onPressedListener: OnPressedListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        if (onPressedListener != null) {
            onPressedListener!!.onPressed(this, pressed)
        }
    }

    public interface OnPressedListener {
        fun onPressed(view: View, pressed: Boolean)
    }

    fun setOnPressedListener(onPressedListener: OnPressedListener) {
        this.onPressedListener = onPressedListener
    }
}
