package com.ekosoftware.financialpreview.data

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import java.util.*

object DummyData1 {

    val accounts = listOf(
        Account(
            id = "ac1 Bapro",
            currencyCode = "ARS",
            name = "CA $ BaPro",
            balance = 28139.96,
            bank = "Banco Provincia",
            colorResId = R.color.materialColor11
        ),
        Account(
            id = "ac2 MP",
            currencyCode = "ARS",
            name = "MercadoPago",
            balance = 14337.2,
            colorResId = R.color.materialColor8
        ),
        Account(
            id = "ac3 bolsillo",
            currencyCode = "ARS",
            name = "$ Bolsillo",
            balance = 1220.0,
            colorResId = R.color.materialColor2
        ),
        Account(
            id = "bapro dols",
            currencyCode = "USD",
            name = "CA USD BaPro",
            balance = 0.08,
            colorResId = R.color.materialColor14
        ),
        Account(
            id = "bolsi dols",
            currencyCode = "USD",
            name = "USD Bolsillo",
            balance = 1690.0,
            colorResId = R.color.materialColor14
        ),
    )

    val categories = listOf(
        Category(
            id = "cat salario",
            name = "Salario",
            iconResId = R.drawable.category_work,
            colorResId = R.color.materialColor12
        ),
        Category(
            id = "cat compras",
            name = "Compras",
            iconResId = R.drawable.category_shopping_cart,
            colorResId = R.color.materialColor10
        ),
        Category(
            id = "cat comida y bebida",
            name = "Comida y bebida",
            iconResId = R.drawable.category_food,
            colorResId = R.color.materialColor16
        ),
        Category(
            id = "cat seguros",
            name = "Seguros",
            iconResId = R.drawable.category_safety,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = "cat sit",
            name = "SIT",
            iconResId = R.drawable.category_tools,
            colorResId = R.color.materialColor6
        ),
        Category(
            id = "cat salud",
            name = "Salud",
            iconResId = R.drawable.category_cards_heart,
            colorResId = R.color.materialColor1
        ),
    )

    val budgets = listOf<Budget>(
        Budget(
            id = "bud master",
            name = "Varios (salidas, etc.)",
            currencyCode = "ARS",
            description = "",
            leftAmount = -4430.0,
            startingAmount = -6000.0,
            frequency = Frequency(
                from = 202101,
                to = 999999,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            )
        )
    )

    val movements = listOf<Movement>(
        Movement(
            id = "mov1",
            leftAmount = 14042.0,
            startingAmount = 14042.0,
            currencyCode = "ARS",
            name = "Sueldo Envión",
            description = "",
            frequency = Frequency(
                from = 202101,
                to = 999999,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            ),
            accountId = "ac1 Bapro",
            categoryId = "cat salario",
            budgetId = null,
        ),
        Movement(
            id = "mov2",
            leftAmount = 14042.0,
            startingAmount = 14042.0,
            currencyCode = "ARS",
            name = "Sueldo PAINA",
            description = "",
            frequency = Frequency(
                from = 202101,
                to = 999999,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            ),
            accountId = "ac1 Bapro",
            categoryId = "cat salario",
            budgetId = null,
        ),
        Movement(
            id = "mov3",
            leftAmount = -1833.33,
            startingAmount = -1833.33,
            currencyCode = "ARS",
            name = "Silla Gamer",
            description = "",
            frequency = Frequency(
                from = 202101,
                to = 202207,
                installments = 18,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            ),
            accountId = "ac1 Bapro",
            categoryId = "cat compras",
            budgetId = null,
        ),
        Movement(
            id = "mov4",
            leftAmount = -1570.00,
            startingAmount = -1570.00,
            currencyCode = "ARS",
            name = "Pedidos Ya con Geor (McDonalds)",
            description = "",
            frequency = Frequency(
                from = 202101,
                to = 202101,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            ),
            accountId = "ac1 Bapro",
            categoryId = "cat comida y bebida",
            budgetId = "bud master"
        ),
        Movement(
            id = "mov5",
            leftAmount = -369.00,
            startingAmount = -369.00,
            currencyCode = "ARS",
            name = "Seguro mala praxis",
            description = "",
            frequency = Frequency(
                from = 202101,
                to = 999999,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            ),
            accountId = "ac1 Bapro",
            categoryId = "cat seguros",
            budgetId = null
        ),
        Movement(
            id = "mov6",
            leftAmount = -500.00,
            startingAmount = -2000.00,
            currencyCode = "ARS",
            name = "Oftalmóloga",
            description = "",
            frequency = Frequency(
                from = 202101,
                to = 202101,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            ),
            accountId = "ac3 bolsillo",
            categoryId = "cat salud",
            budgetId = null
        ),
    )

    val settleGroups = listOf(
        SettleGroup(
            id = "settle 1",
            name = "MasterCard",
            description = "",
            percentage = 0.012,
            currencyCode = "ARS"
        ),
    )

    val settleGroupMovementsCrossRef = listOf(
        SettleGroupMovementsCrossRef(1, 3),
        SettleGroupMovementsCrossRef(1, 4),
        SettleGroupMovementsCrossRef(1, 5)
    )

    /*val records = listOf<Record>(
        Record(
            id = "record oftal #1",
            date = Date(Calendar.getInstance().apply {
                set(2020, 12, 5)
            }.timeInMillis),
            name = "Oftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = 1,
            amount = -1500.0,
            movement = Movement(
                id = "mov6",
                leftAmount = -2000.00,
                startingAmount = -2000.00,
                currencyCode = "ARS",
                name = "Oftalmóloga",
                description = "",
                frequency = Frequency(
                    from = 202101,
                    to = 202101,
                    installments = null,
                    monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
                ),
                accountId = "ac3 bolsillo",
                categoryId = "cat salud",
                budgetId = null
            )
        )
    )*/

}