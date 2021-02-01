package com.ekosoftware.financialpreview.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(message, duration)
}

fun View.snack(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
) {
    Snackbar.make(this, message, duration).show()
}

fun Context.stringResource(resIntId: Int, vararg formatArgs: Any?): String {
    return this.getString(resIntId, *formatArgs)
}

fun Fragment.stringResource(resIntId: Int, vararg formatArgs: Any?): String {
    return requireContext().stringResource(resIntId, *formatArgs)
}


/**
 * Retrieve a color from the current [android.content.res.Resources.Theme].
 */
@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

