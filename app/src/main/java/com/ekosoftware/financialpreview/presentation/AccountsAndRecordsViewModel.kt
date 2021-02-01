package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.record.RecordSummary
import com.ekosoftware.financialpreview.domain.local.AccountsAndRecordsRepository
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import java.util.*
import javax.inject.Inject


@HiltViewModel
class AccountsAndRecordsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val accountsAndRecordsRepository: AccountsAndRecordsRepository,
     private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val ACCOUNTS_SEARCH_STRING_KEY = "accountsSearchStringKey"
        private const val SELECTED_FILTER_OPTIONS_KEY = "selectedFilterOptionsKey"
    }

    private val accountSearchPhrase = savedStateHandle.getLiveData(ACCOUNTS_SEARCH_STRING_KEY, "")

    fun accountSearch(phrase: String) {
        accountSearchPhrase.value = phrase
    }

    /*val accounts = accountSearchPhrase.switchMap { searchPhrase ->
        resourceIOLiveData { accountsAndRecordsRepository.getAccounts(searchPhrase) }
    }*/

    val accounts = accountSearchPhrase.switchToResourceIOLiveData { searchPhrase ->
        accountsAndRecordsRepository.getAccounts(searchPhrase)
    }

    val recordsFilterOptions: MutableLiveData<RecordsFilterOptions> =
        savedStateHandle.getLiveData(SELECTED_FILTER_OPTIONS_KEY, RecordsFilterOptions())

    fun recordSearch(phrase: String) {
        val old = currentFilterOptions
        setFilterOptions(
            RecordsFilterOptions(
                phrase,
                old.accountId,
                old.topDate,
                old.amountMax,
                old.amountMin
            )
        )
    }

    fun setFilterOptions(recordsFilterOptions: RecordsFilterOptions) {
        this.recordsFilterOptions.value = recordsFilterOptions
    }

    val currentFilterOptions get() = recordsFilterOptions.value!!

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
    var searchString: String = "",
    var accountId: String = "",
    var topDate: Date = Date(),
    var amountMax: Double = -100_000.0,
    var amountMin: Double = 100_000.0
)