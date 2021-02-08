package com.ekosoftware.financialpreview.presentation

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.record.RecordUI
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
import com.ekosoftware.financialpreview.domain.local.AccountsAndRecordsRepository
import com.ekosoftware.financialpreview.util.getDaysAgo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import java.util.*
import javax.inject.Inject


@HiltViewModel
class AccountsAndRecordsViewModel @Inject constructor(
    private val accountsAndRecordsRepository: AccountsAndRecordsRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val ACCOUNTS_SEARCH_STRING_KEY = "accountsSearchStringKey"
        private const val SELECTED_FILTER_OPTIONS_KEY = "selectedFilterOptionsKey"
    }

    init {
        setFilterOptions(RecordsFilterOptions())
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

    private val recordsFilterOptions: MutableLiveData<RecordsFilterOptions> =
        savedStateHandle.getLiveData(SELECTED_FILTER_OPTIONS_KEY, RecordsFilterOptions())

    fun recordSearch(query: String) {
        Log.d("RECORDS", "recordSearch -> $query")
        val old: RecordsFilterOptions? = savedStateHandle[SELECTED_FILTER_OPTIONS_KEY]
        setFilterOptions(
            old?.apply {
                searchString = query
            } ?: RecordsFilterOptions(searchString = query)
        )
    }

    fun setFilterOptions(recordsFilterOptions: RecordsFilterOptions) {
        savedStateHandle[SELECTED_FILTER_OPTIONS_KEY] = recordsFilterOptions
        //this.recordsFilterOptions.value = recordsFilterOptions
    }

    private var _records: LiveData<Resource<List<RecordUIShort>>>? = null

    fun records(accountId: String): LiveData<Resource<List<RecordUIShort>>> =
        _records ?: recordsFilterOptions.switchMap { options ->
            Log.d("RECORDS", "options -> $options")
            liveData<Resource<List<RecordUIShort>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
                Log.d("RECORDS", "records: Looking for some records")
                Log.d("RECORDS", "records: $options")
                Log.d("RECORDS", "records: -$accountId-")
                emitSource(accountsAndRecordsRepository.getRecords(
                    accountId,
                    options.searchString,
                    options.topDate,
                    options.amountMax,
                    options.amountMin
                ).map {
                    Resource.Success(it)
                })
            }
        }.also {
            _records = it
        }

}

@Parcelize
data class RecordsFilterOptions(
    var searchString: String = "",
    var topDate: Date = getDaysAgo(1000),
    var amountMax: Long = 1_000_000_000_000,
    var amountMin: Long = -1_000_000_000_000
) : Parcelable