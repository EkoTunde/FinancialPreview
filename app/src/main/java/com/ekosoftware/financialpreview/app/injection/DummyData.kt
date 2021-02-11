package com.ekosoftware.financialpreview.app.injection

import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.currency.Currency
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import java.util.*

object DummyData {

    val m0Currencies = arrayOf(
        Currency(
            "ARS",
            true,
            1.0
        ),
        Currency(
            "USD",
            true,
            92.5
        )
    )

    val m1Accounts = arrayOf(
        Account(
            id = "ac1 Bapro",
            currencyCode = "ARS",
            name = "CA $ BaPro",
            balance = 281399600,
            bank = "Banco Provincia",
            colorResId = R.color.materialColor11
        ),
        Account(
            id = "ac2 MP",
            currencyCode = "ARS",
            name = "MercadoPago",
            balance = 143372000,
            colorResId = R.color.materialColor8
        ),
        Account(
            id = "ac3 bolsillo",
            currencyCode = "ARS",
            name = "$ Bolsillo",
            balance = 1220000,
            colorResId = R.color.materialColor2
        ),
        Account(
            id = "bapro dols",
            currencyCode = "USD",
            name = "CA USD BaPro",
            balance = 800,
            colorResId = R.color.materialColor14
        ),
        Account(
            id = "bolsi dols",
            currencyCode = "USD",
            name = "USD Bolsillo",
            balance = 16_900_000,
            colorResId = R.color.materialColor14
        ),
    )

    val m2Categories = arrayOf(
        Category(
            id = "cat salario",
            name = "Salario",
            description = "",
            iconResId = R.drawable.category_work,
            colorResId = R.color.materialColor12
        ),
        Category(
            id = "cat compras",
            name = "Compras",
            description = "",
            iconResId = R.drawable.category_shopping_cart,
            colorResId = R.color.materialColor10
        ),
        Category(
            id = "cat comida y bebida",
            name = "Comida y bebida",
            description = "",
            iconResId = R.drawable.category_food,
            colorResId = R.color.materialColor16
        ),
        Category(
            id = "cat seguros",
            name = "Seguros",
            description = "",
            iconResId = R.drawable.category_safety,
            colorResId = R.color.materialColor4
        ),
        Category(
            id = "cat sit",
            name = "SIT",
            description = "",
            iconResId = R.drawable.category_tools,
            colorResId = R.color.materialColor6
        ),
        Category(
            id = "cat salud",
            name = "Salud",
            description = "",
            iconResId = R.drawable.category_cards_heart,
            colorResId = R.color.materialColor1
        ),
    )

    val m3Budgets = arrayOf<Budget>(
        Budget(
            id = "bud master",
            name = "Varios (salidas, etc.)",
            currencyCode = "ARS",
            description = "",
            leftAmount = -44300000,
            startingAmount = -60000000,
            frequency = Frequency(
                from = 202101,
                to = 999999,
                installments = null,
                monthsChecked = "Jan-Feb-Mar-Apr-May-Jun-Jul-Aug-Sep-Oct-Nov-Dec"
            )
        )
    )

    val m4Movements = arrayOf<Movement>(
        Movement(
            id = "mov1",
            leftAmount = 140420000,
            startingAmount = 140420000,
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
            leftAmount = 140420000,
            startingAmount = 140420000,
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
            leftAmount = -18333300,
            startingAmount = -18333300,
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
            leftAmount = -15700000,
            startingAmount = -15_700_000,
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
            leftAmount = -3690000,
            startingAmount = -3690000,
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
            leftAmount = -5000000,
            startingAmount = -20000000,
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

    val m5SettleGroups = arrayOf(
        SettleGroup(
            id = "settle 1",
            name = "MasterCard",
            description = "",
            percentage = 1.2,
            colorResId = R.color.materialColor12
        ),
    )

    val m6SettleGroupMovementsCrossRef = arrayOf(
        SettleGroupMovementsCrossRef("settle 1", "mov3"),
        SettleGroupMovementsCrossRef("settle 1", "mov4"),
        SettleGroupMovementsCrossRef("settle 1", "mov5"),
        SettleGroupMovementsCrossRef("settle 1", "mov6")
    )

    val m7Records = arrayOf<Record>(
        Record(
            id = "record oftal #1",
            date = Date(Calendar.getInstance().apply {
                set(2021, 2, 9)
            }.timeInMillis),
            name = "aOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -7_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #2",
            date = Date(Calendar.getInstance().apply {
                set(2021, 1, 25)
            }.timeInMillis),
            name = "bOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -8_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #3",
            date = Date(Calendar.getInstance().apply {
                set(2021, 1, 1)
            }.timeInMillis),
            name = "cOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -9_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #4",
            date = Date(Calendar.getInstance().apply {
                set(2020, 12, 8)
            }.timeInMillis),
            name = "dOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -10_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #5",
            date = Date(Calendar.getInstance().apply {
                set(2020, 11, 9)
            }.timeInMillis),
            name = "eOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -11_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #6",
            date = Date(Calendar.getInstance().apply {
                set(2020, 1, 1)
            }.timeInMillis),
            name = "fOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -12_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #7",
            date = Date(Calendar.getInstance().apply {
                set(2020, 12, 11)
            }.timeInMillis),
            name = "gOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -13_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #8",
            date = Date(Calendar.getInstance().apply {
                set(2020, 12, 12)
            }.timeInMillis),
            name = "hOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -14_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        ),
        Record(
            id = "record oftal #9",
            date = Date(Calendar.getInstance().apply {
                set(2020, 12, 13)
            }.timeInMillis),
            name = "iOftalmóloga (pago parcial)",
            currencyCode = "ARS",
            accountId = "ac1 Bapro",
            amount = -15_000_000,
            movement = Movement(
                id = "mov6",
                leftAmount = -20_000_000,
                startingAmount = -20_000_000,
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
            currentPendingMovementId = "",
            accountIdOut = "",
            categoryId = "",
            debtorName = "",
            lenderName = "",
            description = ""
        )
    )

}