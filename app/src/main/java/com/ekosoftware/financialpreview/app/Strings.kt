package com.ekosoftware.financialpreview.app

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }

    fun getQuantity(
        @PluralsRes pluralsRes: Int,
        quantity: Int,
        vararg formatArgs: Any = emptyArray()
    ): String {
        return App.instance.resources.getQuantityString(pluralsRes, quantity, *formatArgs)
    }
}

