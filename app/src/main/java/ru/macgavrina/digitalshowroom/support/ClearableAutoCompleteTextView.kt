package ru.macgavrina.digitalshowroom.support

import android.view.MotionEvent
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AutoCompleteTextView
import ru.macgavrina.digitalshowroom.MainApplication
import ru.macgavrina.digitalshowroom.R


class ClearableAutoCompleteTextView : AutoCompleteTextView {
    // was the text just cleared?
    internal var justCleared = false

    // if not set otherwise, the default clear listener clears the text in the
    // text view
    private val defaultClearListener: OnClearListener = object : OnClearListener {

        override fun onClear() {
            val et = this@ClearableAutoCompleteTextView
        }
    }

    private var onClearListener = defaultClearListener

    // The image we defined for the clear button
    var imgClearButton = MainApplication.applicationContext().getDrawable(R.drawable.clear_icon)!!

    interface OnClearListener {
        fun onClear()
    }

    /* Required methods, not used in this implementation */
    constructor(context: Context) : super(context) {
        init()
    }

    /* Required methods, not used in this implementation */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    /* Required methods, not used in this implementation */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    internal fun init() {
        // Set the bounds of the button

        // if the clear button is pressed, fire up the handler. Otherwise do nothing
        this.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {

                setCompoundDrawablesWithIntrinsicBounds(
                    null, null,
                    imgClearButton, null
                )

                val et = this@ClearableAutoCompleteTextView

                if (et.compoundDrawables[2] == null)
                    return false

                if (event.action != MotionEvent.ACTION_UP)
                    return false

                if (event.x > et.width - et.paddingRight - imgClearButton.intrinsicWidth) {
                    onClearListener.onClear()
                    justCleared = true
                }
                return false
            }
        })
    }

    fun setOnClearListener(clearListener: OnClearListener) {
        this.onClearListener = clearListener
    }

    fun hideClearButton() {
        this.setCompoundDrawables(null, null, null, null)
    }

    fun showClearButton() {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null)
    }

}