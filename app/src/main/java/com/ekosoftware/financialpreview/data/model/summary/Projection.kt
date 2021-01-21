package com.ekosoftware.financialpreview.data.model.summary

import java.util.*

data class Projection(val currency: String, val currentMonthBalance: Double, val sixMonthsBalance: Double, val yearBalance: Double)
