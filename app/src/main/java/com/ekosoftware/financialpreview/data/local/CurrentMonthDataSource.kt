package com.ekosoftware.financialpreview.data.local

import com.ekosoftware.financialpreview.data.local.daos.CurrencyConversionDao
import com.ekosoftware.financialpreview.data.local.daos.MovementDao
import com.ekosoftware.financialpreview.data.local.daos.ScheduledDao
import javax.inject.Inject

class CurrentMonthDataSource @Inject constructor(
    private val currencyConversionDao: CurrencyConversionDao,
    private val movementDao: MovementDao,
    private val scheduledDao: ScheduledDao,
) {

    // Saldos totales para diferentes monedas

    // Gastos programadas limitados a lo indicado

    // Presupuestos y lo que queda de ellos

    // Impuestos

    // Promedio categorias

}