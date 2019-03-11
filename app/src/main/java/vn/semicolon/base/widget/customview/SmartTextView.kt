package vn.semicolon.base.widget.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet


open class SmartTextView : SmartEditText {

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        this.inputType = InputType.TYPE_NULL
        this.keyListener = null
    }

}