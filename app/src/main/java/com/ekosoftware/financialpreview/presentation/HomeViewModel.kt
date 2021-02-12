package com.ekosoftware.financialpreview.presentation

import android.os.Parcelable
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.app.injection.DummyData
import com.ekosoftware.financialpreview.data.model.HomeData
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovementsCount
import com.ekosoftware.financialpreview.repository.MainRepository
import com.ekosoftware.financialpreview.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    init {
        insertSome()
    }

    private fun insertSome() =
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
            mainRepository.insertSettleGroups(*DummyData.m6SettleGroupMovementsCrossRef)
        }

    companion object {
        private const val SELECTED_CONFIG_KEY = "selectedConfigurationKey"

        private fun localeCurrencyCode(): String {
            return Currency.getInstance(Locale.getDefault()).currencyCode
        }
    }

    @Parcelize
    data class HomeScreenConfiguration(
        var searchPhrase: String = "",
        var currencyCode: String = "ARS"/*localeCurrencyCode()*/,
        var currentYearMonth: Int = 202102/*currentYearMonth()*/,
        var lastUpdate: Long = 0
    ) : Parcelable

    private val selectedConfiguration =
        savedStateHandle.getLiveData(SELECTED_CONFIG_KEY, HomeScreenConfiguration())

    fun submitSearchQuery(query: String) {
        val old: HomeScreenConfiguration? = savedStateHandle[SELECTED_CONFIG_KEY]
        savedStateHandle[SELECTED_CONFIG_KEY] = old.apply {
            this?.searchPhrase = query
        } ?: HomeScreenConfiguration()
    }

    fun setConfiguration(currencyCode: String, currentYearMonth: Int) {
        val old: HomeScreenConfiguration? = savedStateHandle[SELECTED_CONFIG_KEY]
        savedStateHandle[SELECTED_CONFIG_KEY] = old.apply {
            this?.currencyCode = currencyCode
            this?.currentYearMonth = currentYearMonth
        } ?: HomeScreenConfiguration()
    }

    fun refresh() {
        val old: HomeScreenConfiguration? = savedStateHandle[SELECTED_CONFIG_KEY]
        savedStateHandle[SELECTED_CONFIG_KEY] = old.apply {
            this?.lastUpdate = System.currentTimeMillis()
        } ?: HomeScreenConfiguration()
    }

    val movements: LiveData<Resource<List<MovementUI>>> =
        selectedConfiguration.switchMap { config ->
            liveData<Resource<List<MovementUI>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
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

    val budgets: LiveData<Resource<List<Budget>>> = selectedConfiguration.switchMap { config ->
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

    private var settleGroupsWithMovementsCount: LiveData<Resource<List<SettleGroupWithMovementsCount>>>? = null

    fun getSettleGroupsWithMovementsCount(): LiveData<Resource<List<SettleGroupWithMovementsCount>>> =
        settleGroupsWithMovementsCount ?: selectedConfiguration.distinctUntilChanged().switchMap { config ->
            liveData<Resource<List<SettleGroupWithMovementsCount>>> {
                emit(Resource.Loading())
                try {
                    emitSource(mainRepository.getSettleGroupsWithMovementsCount(config.currencyCode, config.currentYearMonth).map {
                        Resource.Success(it)
                    })
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }.also {
            settleGroupsWithMovementsCount = it
        }

    private var _homeData: LiveData<Resource<HomeData>>? = null

    fun getHomeData(): LiveData<Resource<HomeData>> = _homeData ?: selectedConfiguration.switchMap { config ->
        liveData<Resource<HomeData>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val months = listOf(0..12).flatten().map {
                    withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                        mainRepository.getMonthPendingSummary(
                            config.currentYearMonth.plusMonths(it),
                            config.currencyCode
                        )
                    }
                }
                emit(Resource.Success(HomeData(config.currencyCode, config.currentYearMonth, months[0].currentAccountsBalance ?: 0, months)))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }.also {
        _homeData = it
    }

    /*val homeData: LiveData<Resource<HomeData>> = selectedConfiguration.switchMap { config ->
        liveData<Resource<HomeData>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val months = listOf(0..12).flatten().map {
                    withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                        mainRepository.getMonthPendingSummary(
                            config.currentYearMonth.plusMonths(it),
                            config.currencyCode
                        )
                    }
                }
                emit(Resource.Success(HomeData(config.currencyCode, config.currentYearMonth, months[0].currentAccountsBalance ?: 0, months)))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }*/
}