package vn.semicolon.base.widget.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


open class SmartTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val helper = SmartTextViewHelper(this)
        helper.loadFromAttributes(attrs, defStyleAttr)
    }

}