package com.ekosoftware.financialpreview.app

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.ekosoftware.financialpreview.R

object Colors {
    fun get(@ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(App.instance, colorResId)
    }

    val colorsResIds = listOf(
        R.color.materialColor1,
        R.color.materialColor2,
        R.color.materialColor3,
        R.color.materialColor4,
        R.color.materialColor5,
        R.color.materialColor6,
        R.color.materialColor7,
        R.color.materialColor8,
        R.color.materialColor9,
        R.color.materialColor10,
        R.color.materialColor11,
        R.color.materialColor12,
        R.color.materialColor13,
        R.color.materialColor14,
        R.color.materialColor15,
        R.color.materialColor16,
    )

    private val progressRangeColors = listOf(
        R.color.materialColor10,
        R.color.materialColor11,
        R.color.materialColor12,
        R.color.materialColor13,
        R.color.materialColor14,
        R.color.materialColor15,
        R.color.materialColor16,
        R.color.materialColor1,
    )

    fun progressColor(progress: Int): Int {
        return get(
            when (progress) {
                in 0..12 -> progressRangeColors[7]
                in 13..25 -> progressRangeColors[6]
                in 26..37 -> progressRangeColors[5]
                in 38..50 -> progressRangeColors[4]
                in 51..62 -> progressRangeColors[3]
                in 63..75 -> progressRangeColors[2]
                in 76..87 -> progressRangeColors[1]
                in 88..100 -> progressRangeColors[0]
                else -> throw IllegalArgumentException("Progress $progress is out of bounds (0..100 range).")
            }
        )
    }
}