package com.ekosoftware.financialpreview.presentation.ui.home.now

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.local.NowRepository
import com.ekosoftware.financialpreview.data.model.CurrencyConversion
import com.ekosoftware.financialpreview.data.model.Movement
import com.ekosoftware.financialpreview.data.model.account.Account
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.util.*

class NowViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val nowRepository: NowRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "NowViewModel"
    }

    fun insertCurrencies() {
        val pesos = CurrencyConversion(name = "ARS", factor = 85.0, lastUpdate = Date())
        val dolares = CurrencyConversion(name = "USD", factor = 1.0, lastUpdate = Date())

        //localDataSource.
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            nowRepository.insertAccount(account)
        }
    }

    val accounts =
        liveData<Resource<List<Account>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            val accountBaProPesos = Account(
                name = "BaPro ARS",
                balance = 28_516.62,
                bank = "Banco Provincia",
                currencyCode = "ARS"
            )

            val accountBaProDolarucos = Account(
                name = "BaPro USD",
                balance = 0.07,
                bank = "Banco Provincia",
                currencyCode = "USD"
            )

            emit(Resource.Loading())
            try {
                /*viewModelScope.launch {
                    localDataSource.insertAccount(accountBaProPesos, accountBaProDolarucos)
                    Log.d(TAG, "accounts: adding them...")
                }
                for (i in 1..5) {
                    delay(1000)
                    Log.d(TAG, "esperando: $i")
                }*/
                emitSource(nowRepository.getAccountBalances().map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    private val movementsOptions = MutableLiveData<MovementsOptions>()

    fun setMovementOptions(movementsOptions: MovementsOptions) {
        this.movementsOptions.value = movementsOptions
    }

    val movements = movementsOptions.distinctUntilChanged().switchMap { options ->
        liveData<Resource<List<Movement>>> {
            /*val movementPagarOSGeor = Movement(
                date = Date(),
                scheduled = null,
                amount = -2980.0,
                accountId = 1,
                currencyCode = "ARS"
            )
            val localDate = LocalDate(2020,5,1)
            val movementViejo = Movement(
                date = localDate.toDate(),
                scheduled = null,
                amount = -500.0,
                accountId = 1,
                currencyCode = "ARS"
            )*/
            emit(Resource.Loading())
            try {
                /*viewModelScope.launch {
                    localDataSource.insertMovement(movementPagarOSGeor, movementViejo)
                    Log.d(TAG, "moviemientos: adding them...")
                }
                for (i in 1..5) {
                    delay(1000)
                    Log.d(TAG, "esperando moviemientos: $i")
                }*/
                emitSource(
                    nowRepository.getMovements(
                        options.fromDate,
                        options.toDate,
                        options.resultAmountLimit
                    ).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun insertAccount(account: Account) = viewModelScope.launch { nowRepository.insertAccount() }

    fun updateAccount(account: Account) =
        viewModelScope.launch { nowRepository.updateAccount(account) }

    fun deleteAccount(account: Account) =
        viewModelScope.launch { nowRepository.deleteAccount(account) }

    fun insertMovement(movement: Movement) = viewModelScope.launch {
        nowRepository.insertMovement(movement)
    }

    fun updateMovement(movement: Movement) = viewModelScope.launch {
        nowRepository.updateMovement(movement)
    }

    fun deleteMovement(movement: Movement) = viewModelScope.launch {
        nowRepository.deleteMovement(movement)
    }
}

data class MovementsOptions(
    var fromDate: Date = LocalDate().minusDays(30).toDate(),
    var toDate: Date = Date(),
    var resultAmountLimit: Int = 20
)