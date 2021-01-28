package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.DummyData
import com.ekosoftware.financialpreview.data.model.HomeData
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.domain.local.MainRepository
import com.ekosoftware.financialpreview.util.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    init {
        insertSome()
    }

    private fun insertSome() = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
        mainRepository.insertSettleGroups(*DummyData.m6SettleGroupMovementsCrossRef)
    }
    companion object {
        private const val SELECTED_CONFIG_KEY = "selectedConfigurationKey"

        private fun localeCurrencyCode(): String {
            return Currency.getInstance(Locale.getDefault()).currencyCode
        }
    }

    data class HomeScreenConfiguration(
        val searchPhrase: String = "",
        val currencyCode: String = "ARS"/*localeCurrencyCode()*/,
        val currentYearMonth: Int = 202101/*currentYearMonth()*/,
        val lastUpdate: Long = 0
    )

    private val selectedConfiguration =
        savedStateHandle.getLiveData(SELECTED_CONFIG_KEY, HomeScreenConfiguration())

    fun submitSearchQuery(query: String) {
        val old = selectedConfiguration.value
        setConfiguration(
            HomeScreenConfiguration(
                query,
                old?.currencyCode ?: localeCurrencyCode(),
                old?.currentYearMonth ?: currentYearMonth()
            )
        )
    }

    fun setConfiguration(homeScreenConfiguration: HomeScreenConfiguration) {
        savedStateHandle[SELECTED_CONFIG_KEY] = homeScreenConfiguration
    }

    val movements = selectedConfiguration.switchMap { config ->
        liveData<Resource<List<MovementSummary>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(mainRepository.getMovementsSummaries(
                    config.searchPhrase, config.currencyCode, config.currentYearMonth
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
                    mainRepository.getBudgets(
                        config.searchPhrase,
                        config.currencyCode,
                        config.currentYearMonth
                    ).map {
                        Resource.Success(it)
                    })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val settleGroups =
        liveData<Resource<List<SettleGroupWithMovements>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(
                    mainRepository.getSettleGroupsWithMovements().map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    val homeData = selectedConfiguration.switchMap { config ->
        liveData<Resource<HomeData>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val months = listOf(0..12).flatten().map {
                    withContext(viewModelScope.coroutineContext + Dispatchers.IO){
                        mainRepository.getMonthPendingSummary(
                            config.currentYearMonth.plusMonths(it),
                            config.currencyCode
                        )
                    }
                }
                val liveData1 = mainRepository.getAccountsTotalForCurrency(config.currencyCode)
                val liveData2 = mainRepository.getSettleGroupsWithMovements()
                val result =
                    liveData1.combineWith(liveData2) { balance, groups ->
                        HomeData(
                            config.currencyCode,
                            config.currentYearMonth,
                            balance ?: 0,
                            months,
                            groups ?: emptyList()
                        )
                    }
                emitSource(result.map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }
}
