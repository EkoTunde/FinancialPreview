package com.ekosoftware.financialpreview.presentation.ui

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.data.model.summary.Balance
import com.ekosoftware.financialpreview.data.model.summary.MonthSummary
import com.ekosoftware.financialpreview.data.model.summary.MovementSummary
import com.ekosoftware.financialpreview.data.model.summary.QuickViewSummary
import com.ekosoftware.financialpreview.domain.local.HomeRepository
import com.ekosoftware.financialpreview.util.currentYearMonth
import com.ekosoftware.financialpreview.util.plusMonths
import com.ekosoftware.financialpreview.util.taxes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val homeRepository: HomeRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val SELECTED_CURRENCY_KEY = "selectedCurrencyKey"
        private const val SELECTED_CONFIG_KEY = "selectedCurrencyKey"
    }

    data class HomeScreenConfiguration(
        val searchPhrase: String,
        val currency: String,
        val currentMonth: Int
    )

    private val selectedConfiguration =
        savedStateHandle.getLiveData(
            SELECTED_CONFIG_KEY,
            HomeScreenConfiguration("", localeCurrencyCode(), currentYearMonth())
        )

    fun setConfiguration(homeScreenConfiguration: HomeScreenConfiguration) {
        savedStateHandle[SELECTED_CONFIG_KEY] = homeScreenConfiguration
    }

    private fun localeCurrencyCode(): String {
        return Currency.getInstance(Locale.getDefault()).currencyCode
    }

    val currentBalance: LiveData<Balance> = selectedConfiguration.switchMap { configuration ->
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

    val pendingBalanceWithoutTaxes: LiveData<MonthSummary> =
        selectedConfiguration.switchMap { configuration ->
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emitSource(
                    homeRepository.getLiveMonthSummary(
                        currentYearMonth(),
                        configuration.currency
                    )
                )
            }
        }

    val settleGroupsWithMovements = pendingBalanceWithoutTaxes.switchMap { oldSummary ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(homeRepository.getSettleGroupsWithMovements().map { oldSummary to it })
        }
    }

    val pendingSummaryWithTaxes: LiveData<MonthSummary> =
        settleGroupsWithMovements.switchMap { pair ->
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                val oldSummary = pair.first
                val groups = pair.second
                emit(
                    MonthSummary(
                        currencyCode = oldSummary.currencyCode,
                        yearMonth = oldSummary.yearMonth,
                        totalIncome = oldSummary.totalIncome?.plus(groups.sumOf { group ->
                            group.taxes(currentYearMonth(), oldSummary.currencyCode) {
                                it.leftAmount >= 0
                            }
                        }) ?: 0.0,
                        totalExpense = oldSummary.totalExpense?.plus(groups.sumOf { group ->
                            group.taxes(currentYearMonth(), oldSummary.currencyCode) {
                                it.leftAmount < 0
                            }
                        }) ?: 0.0
                    )
                )
                /*emitSource(
                    homeRepository.getSettleGroupsWithMovements().map { groups ->
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
                )*/
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
                // Map a list corresponding to the sequence = {1,2,3,...,10}
                mutableListOf(1..12).flatten().map {
                    // To result of adding each number in sequence to current month (as if month + n)
                    currentMonth.yearMonth.plusMonths(it)
                }.map { yearMonth ->
                    // Map them again to the result of searching for balance summary in repo with yearMonth (previous mapping) as a filter
                    withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                        homeRepository.getMonthPendingSummary(yearMonth, currentMonth.currencyCode)
                    }
                }.toMutableList().apply {
                    // Parse to mutable so adding current month at list's start is possible
                    add(0, currentMonth)
                }.toQuickViewSummary().also {
                    emit(Resource.Success(it))
                }
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    // Map each property as a list to prepare the final QuickViewSummary
    private fun MutableList<MonthSummary>.toQuickViewSummary(): QuickViewSummary {
        // Temp incremental var that holds the differences as they grow
        // Begins with current accounts total balance
        var currentDifferencesAmount = currentBalance.value?.total ?: 0.0
        return QuickViewSummary(
            currencyCode = this[0].currencyCode,
            months = map { month -> month.monthName(appContext) ?: "" },
            incomes = map { month -> month.totalIncome ?: 0.0 },
            expenses = map { month -> month.totalExpense ?: 0.0 },
            balances = map { month -> month.totalBalance },
            accumulatedBalances = listOf(0..12).flatten()
                .map { index -> // Sums the differences from previous months
                    currentDifferencesAmount += this[index].totalBalance
                    return@map currentDifferencesAmount
                })
    }

    val movements = selectedConfiguration.switchMap { config ->
        liveData<Resource<List<MovementSummary>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(homeRepository.getMovementsSummaries(
                    config.searchPhrase, config.currency, config.currentMonth
                ).map {
                    Resource.Success(it)
                })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val budgets = selectedConfiguration.switchMap { config ->
        liveData<Resource<List<Budget>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(
                    homeRepository.getBudgets(
                        config.searchPhrase,
                        config.currency,
                        config.currentMonth
                    ).map {
                        Resource.Success(it)
                    })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val settleGroups = settleGroupsWithMovements.switchMap { result ->
        liveData<Resource<List<SettleGroupWithMovements>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(result.second))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }
}