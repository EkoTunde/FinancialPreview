package com.ekosoftware.financialpreview.presentation.ui.home

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.Balance
import com.ekosoftware.financialpreview.data.model.summary.MonthSummary
import com.ekosoftware.financialpreview.data.model.summary.QuickViewSummary
import com.ekosoftware.financialpreview.domain.local.HomeRepository
import com.ekosoftware.financialpreview.util.currentYearMonth
import com.ekosoftware.financialpreview.util.plusMonths
import com.ekosoftware.financialpreview.util.taxes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val homeRepository: HomeRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val SELECTED_CURRENCY_KEY = "selectedCurrencyKey"
        private const val SELECTED_CONFIG_KEY = "selectedCurrencyKey"
    }

    data class HomeScreenConfiguration(val currency: String, val currentMonth: Int)

    private val selectedCurrency =
        savedStateHandle.getLiveData(
            SELECTED_CONFIG_KEY,
            HomeScreenConfiguration(localeCurrencyCode(), currentYearMonth())
        )

    fun setConfiguration(homeScreenConfiguration: HomeScreenConfiguration) {
        savedStateHandle[SELECTED_CONFIG_KEY] = homeScreenConfiguration
    }

    private fun localeCurrencyCode(): String {
        return Currency.getInstance(Locale.getDefault()).currencyCode
    }

    val currentBalance: LiveData<Balance> = selectedCurrency.switchMap { configuration ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(
                homeRepository.getAccountsTotalForCurrency(configuration.currency).map { total ->
                    Balance(total ?: 0.0, configuration.currency)
                }
            )
        }
    }


    val balance: LiveData<Resource<Balance>> =
        currentBalance.distinctUntilChanged().switchMap { balance ->
            liveData<Resource<Balance>>(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(Resource.Loading())
                try {
                    emit(Resource.Success(balance))
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }

    val pendingBalanceWithoutTaxes: LiveData<MonthSummary> = selectedCurrency.switchMap { configuration ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(
                homeRepository.getPendingSummaryWithoutTaxes(
                    currentYearMonth(),
                    configuration.currency
                )
            )
        }
    }

    val pendingSummaryWithTaxes: LiveData<MonthSummary> =
        pendingBalanceWithoutTaxes.switchMap { oldSummary ->
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emitSource(
                    homeRepository.getTaxesForSettleGroups().map { groups ->
                        MonthSummary(
                            oldSummary.currencyCode,
                            oldSummary.yearMonth,
                            oldSummary.totalIncome?.plus(groups.sumOf { group ->
                                group.taxes(currentYearMonth(), oldSummary.currencyCode) {
                                    it.leftAmount >= 0
                                }
                            }) ?: 0.0,
                            oldSummary.totalExpense?.plus(groups.sumOf { group ->
                                group.taxes(currentYearMonth(), oldSummary.currencyCode) {
                                    it.leftAmount < 0
                                }
                            }) ?: 0.0
                        )
                    }
                )
            }
        }

    val monthSummary: LiveData<Resource<MonthSummary>> =
        pendingSummaryWithTaxes.distinctUntilChanged().switchMap { pendingSummary ->
            liveData<Resource<MonthSummary>>(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(Resource.Loading())
                try {
                    emit(Resource.Success(pendingSummary))
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }

    private val currentMonth = MutableLiveData<Int>()

    fun setCurrentMonth(newMonth: Int) {
        currentMonth.value = newMonth
    }

    val quickViewSummary = pendingSummaryWithTaxes.switchMap { currentMonth ->
        liveData<Resource<QuickViewSummary>>(viewModelScope.coroutineContext + Dispatchers.IO) {

            emit(Resource.Loading())
            try {
                listOf(1..12).flatten().map {
                    currentMonth.yearMonth.plusMonths(it)
                }.map { yearMonth ->
                    // Map them again to the result of searching for balance summary in repo
                    withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                        homeRepository.getMonthPendingSummary(
                            yearMonth,
                            currentMonth.currencyCode
                        )
                    }
                }.also { months ->

                    val monthsNames = months.map { month -> month.monthName(appContext) ?: "" }
                    val incomes = months.map { month -> month.totalIncome ?: 0.0 }
                    val expenses = months.map { month -> month.totalExpense ?: 0.0 }
                    val balances = months.map { month -> month.totalBalance }

                    var currentDifferencesAmount = currentBalance.value?.total ?: 0.0
                    val differences = listOf(0..12).flatten().map { index ->
                        currentDifferencesAmount += months[index].totalBalance
                        return@map currentDifferencesAmount
                    }

                    emit(
                        Resource.Success(
                            QuickViewSummary(
                                currentMonth.currencyCode,
                                monthsNames,
                                incomes,
                                expenses,
                                balances,
                                differences
                            )
                        )
                    )

                }
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }
}

/*val dataLoadingFlags = MutableLiveData<DataLoadingFlags>()

    fun updateDataLoadingFlags(
        balanceFlag: Boolean? = false,
        pendingFlag: Boolean? = false,
        projectionsFlag: Boolean? = false,
        quickViewFlag: Boolean? = false
    ) {
        val currentState = dataLoadingFlags.value ?: DataLoadingFlags()
        val newState = DataLoadingFlags(
            balanceFlag ?: currentState.balanceFlag,
            pendingFlag ?: currentState.pendingFlag,
            projectionsFlag ?: currentState.projectionsFlag,
            quickViewFlag ?: currentState.quickViewFlag
        )
        dataLoadingFlags.value = newState
    }

    val isLoadingComplete = dataLoadingFlags.distinctUntilChanged().switchMap { dataFlags ->
        liveData { if (dataFlags.isLoadingComplete()) emit(true) }
    }

data class MovementsOptions(
    var fromDate: Date = LocalDate().minusDays(30).toDate(),
    var toDate: Date = Date(),
    var resultAmountLimit: Int = 20
)

data class DataLoadingFlags(
    var balanceFlag: Boolean = false,
    var pendingFlag: Boolean = false,
    var projectionsFlag: Boolean = false,
    var quickViewFlag: Boolean = false
) {
    fun isLoadingComplete(): Boolean =
        balanceFlag && pendingFlag && projectionsFlag && quickViewFlag
}*/

/*
val pendingTaxTotal = selectedCurrency.switchMap { currency ->
        liveData<PendingSummary>(viewModelScope.coroutineContext + Dispatchers.IO) {
            homeRepository.getTaxesForSettleGroups().map {
                it.summary(currentYearMonth(), currency)
            }
        }
    }

    val pendingIncomesTotal = selectedCurrency.switchMap { currency ->
        liveData<Double>(viewModelScope.coroutineContext + Dispatchers.IO) {
            homeRepository.getCurrentPendingIncome(currentYearMonth(), currency)
        }
    }

    val pendingExpensesTotal = selectedCurrency.switchMap { currency ->
        liveData<Double>(viewModelScope.coroutineContext + Dispatchers.IO) {
            val s = homeRepository.getTaxesForSettleGroups().map {
                it.sumOf { groupWithMovements ->
                    groupWithMovements.taxes(currentYearMonth(), currency) { movement ->
                        movement.leftAmount >= 0
                    }
                }
            }

            homeRepository.getCurrentPendingExpense(currentYearMonth(), currency)
        }
    }
 */