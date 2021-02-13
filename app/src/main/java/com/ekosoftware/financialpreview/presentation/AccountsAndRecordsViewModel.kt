package com.ekosoftware.financialpreview.presentation

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
import com.ekosoftware.financialpreview.repository.AccountsAndRecordsRepository
import com.ekosoftware.financialpreview.util.getDateDaysAgo
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
        private const val RECORDS_SEARCH_STRING_KEY = "recordsSearchStringKey"
        private const val SELECTED_FILTER_OPTIONS_KEY = "selectedFilterOptionsKey"

        private const val OPTION_AMOUNT_ASC = 0
        private const val OPTION_AMOUNT_DESC = 1
        private const val OPTION_DATE_ASC = 2
        private const val OPTION_DATE_DESC = 3
        private const val OPTION_NAME_ASC = 4
        private const val OPTION_NAME_DESC = 5

        const val ORDER_BY_AMOUNT_ASC = "amountAsc"
        private const val ORDER_BY_AMOUNT_DESC = "amountDesc"
        const val ORDER_BY_DATE_ASC = "dateAsc"
        private const val ORDER_BY_DATE_DESC = "dateDesc"
        private const val ORDER_BY_NAME_ASC = "nameAsc"
        private const val ORDER_BY_NAME_DESC = "nameDesc"
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

    fun setRecordsOrderBy(position: Int) {
        val newOrderBy = when (position) {
            OPTION_AMOUNT_ASC -> ORDER_BY_AMOUNT_ASC
            OPTION_AMOUNT_DESC -> ORDER_BY_AMOUNT_DESC
            OPTION_DATE_ASC -> ORDER_BY_DATE_ASC
            OPTION_DATE_DESC -> ORDER_BY_DATE_DESC
            OPTION_NAME_ASC -> ORDER_BY_NAME_ASC
            OPTION_NAME_DESC -> ORDER_BY_NAME_DESC
            else -> throw IllegalArgumentException("Provided $position isn't a valid position.")
        }
        val old: RecordsFilterOptions? = savedStateHandle[SELECTED_FILTER_OPTIONS_KEY]
        setFilterOptions(
            old?.apply {
                orderBy = newOrderBy
            } ?: RecordsFilterOptions(orderBy = newOrderBy)
        )
    }

    fun setMinValue(text: CharSequence?) {
        val old: RecordsFilterOptions? = savedStateHandle[SELECTED_FILTER_OPTIONS_KEY]
        setFilterOptions(
            old?.apply {
                amountMin = try {
                    text.toString().toLong() * 10_000
                } catch (e: Exception) {
                    Long.MIN_VALUE
                }
            } ?: RecordsFilterOptions(amountMin = text.toString().toLong() * 10_000)
        )
    }

    fun setMaxValue(text: CharSequence?) {
        val old: RecordsFilterOptions? = savedStateHandle[SELECTED_FILTER_OPTIONS_KEY]
        setFilterOptions(
            old?.apply {
                amountMax = try {
                    text.toString().toLong() * 10_000
                } catch (e: Exception) {
                    Long.MAX_VALUE
                }
            } ?: RecordsFilterOptions(amountMin = text.toString().toLong() * 10_000)
        )
    }

    fun setAmountOfDaysFilter(days: Int) {
        val old: RecordsFilterOptions? = savedStateHandle[SELECTED_FILTER_OPTIONS_KEY]
        setFilterOptions(
            old?.apply {
                topDate = getDateDaysAgo(days)
            } ?: RecordsFilterOptions(topDate = getDateDaysAgo(days))
        )
    }

    fun setFilterOptions(recordsFilterOptions: RecordsFilterOptions) {
        savedStateHandle[SELECTED_FILTER_OPTIONS_KEY] = recordsFilterOptions
        //this.recordsFilterOptions.value = recordsFilterOptions
    }

    private val recordsSearchString = savedStateHandle.getLiveData<String>(RECORDS_SEARCH_STRING_KEY, "")


    private var _records: LiveData<List<RecordUIShort>>? = null

    fun records(accountId: String): LiveData<List<RecordUIShort>> =
        _records ?: recordsFilterOptions.switchMap { options ->
            Log.d("SET DATA", "records: $options")
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emitSource(
                    accountsAndRecordsRepository.getRecords(
                        accountId,
                        options.searchString,
                        options.topDate,
                        options.amountMax,
                        options.amountMin,
                        options.orderBy
                    )
                )
            }
        }.also {
            _records = it
        }

    fun clearSelectedData() {
        _records = null
    }
}

@Parcelize
data class RecordsFilterOptions(
    var searchString: String = "",
    var topDate: Date = getDateDaysAgo(7),
    var amountMax: Long = Long.MAX_VALUE,
    var amountMin: Long = Long.MIN_VALUE,
    var orderBy: String = AccountsAndRecordsViewModel.ORDER_BY_DATE_ASC
) : Parcelable