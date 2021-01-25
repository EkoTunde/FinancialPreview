package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.record.RecordSummary
import com.ekosoftware.financialpreview.domain.local.AccountsAndRecordsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import java.util.*

class AccountsAndRecordsViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val accountsAndRecordsRepository: AccountsAndRecordsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val ACCOUNTS_SEARCH_STRING_KEY = "accountsSearchStringKey"
        private const val SELECTED_FILTER_OPTIONS_KEY = "selectedFilterOptionsKey"
    }

    private val accountSearchPhrase = savedStateHandle.getLiveData(ACCOUNTS_SEARCH_STRING_KEY, "")

    fun accountSearch(phrase: String) {
        accountSearchPhrase.value = phrase
    }

    val accounts = accountSearchPhrase.switchMap { searchPhrase ->
        liveData<Resource<List<Account>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(accountsAndRecordsRepository.getAccounts(searchPhrase).map {
                    Resource.Success(it ?: emptyList())
                })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val recordsFilterOptions: MutableLiveData<RecordsFilterOptions> =
        savedStateHandle.getLiveData(SELECTED_FILTER_OPTIONS_KEY)

    fun setFilterOptions(recordsFilterOptions: RecordsFilterOptions) {
        this.recordsFilterOptions.value = recordsFilterOptions
    }

    val records = recordsFilterOptions.distinctUntilChanged().switchMap { options ->
        liveData<Resource<List<RecordSummary>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(
                    accountsAndRecordsRepository.getRecords(
                        options.accountId,
                        options.topDate,
                        options.amountMax,
                        options.amountMin
                    ).map {
                        Resource.Success(it)
                    })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

}

data class RecordsFilterOptions(
    var accountId: String = "",
    var topDate: Date,
    var amountMax: Double = -100_000.0,
    var amountMin: Double = 100_000.0
)