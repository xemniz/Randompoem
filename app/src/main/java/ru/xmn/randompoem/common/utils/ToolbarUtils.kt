package ru.xmn.randompoem.common.utils

import android.content.Context
import android.util.TypedValue

object ToolbarUtils{
    fun statusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun actionBarHeight(context: Context): Int {
        val tv = TypedValue()
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)
        return TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
    }
}