package vn.semicolon.base.widget.customview

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View

class SmartViewHelper {
    companion object {
        fun setView(
            view: View,
            shape: Int? = null,
            radius: Float? = null,
            borderWidth: Int? = null,
            borderColor: Int? = null,
            dashWidth: Float? = null,
            dashGap: Float? = null,
            fillColor: Int? = null,
            rippleColor: Int? = null
        ) {
            var drawShape = view.background as? GradientDrawable
            if(drawShape == null){
                drawShape = GradientDrawable()
            }
            if (shape != null)
                drawShape.shape = shape

            if (radius != null)
                drawShape.cornerRadius = radius

            if (borderWidth != null) // if set width, init color
                drawShape.setStroke(
                    borderWidth,
                    borderColor ?: Color.BLACK,
                    dashWidth ?: 0f,
                    dashGap ?: 0f
                )
            else if (borderColor != null) // if set color, init width
                drawShape.setStroke(
                    1,
                    borderColor,
                    dashWidth ?: 0f,
                    dashGap ?: 0f
                )

            if (fillColor != null && fillColor != Color.TRANSPARENT)
                drawShape.setColor(fillColor)
            val rippleBackground = RippleDrawable(
                getPressedColorSelector(
                    Color.parseColor("#00000000"),
                    rippleColor ?: Color.TRANSPARENT
                ),
                drawShape, drawShape
            )
            view.background = rippleBackground
        }


        private fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
            return ColorStateList(
                arrayOf(
                    intArrayOf(),
                    intArrayOf(android.R.attr.state_pressed)
                ),
                intArrayOf(pressedColor, pressedColor, pressedColor, normalColor)
            )
        }
    }

}