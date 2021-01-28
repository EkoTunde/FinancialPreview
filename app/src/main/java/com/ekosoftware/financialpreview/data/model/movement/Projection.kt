package com.ekosoftware.financialpreview.data.model.movement

data class Projection(val currency: String, val currentMonthBalance: Long, val sixMonthsBalance: Long, val yearBalance: Long)
