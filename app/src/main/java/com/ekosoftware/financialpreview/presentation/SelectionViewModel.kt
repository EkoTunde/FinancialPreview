package com.ekosoftware.financialpreview.presentation


import androidx.lifecycle.*
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.domain.local.SelectionRepository
import com.ekosoftware.financialpreview.util.forDisplayAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val selectionRepository: SelectionRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val ACCOUNTS_SEARCH_STRING_KEY = "accountsSearchStringKey"
        private const val SEARCH_TEXT_KEY = "accountsSearchStringKey"
        private const val ACCOUNT_ID_KEY = "SelectionAccountIdKey"
        private const val BUDGET_ID_KEY = "SelectionBudgetIdKey"
        private const val CATEGORY_ID_KEY = "SelectionCategoryIdKey"
        private const val MOVEMENT_ID_KEY = "SelectionMovementIdKey"
        private const val SETTLE_GROUP_ID_KEY = "SelectionSettleGroupIdKey"

        const val ACCOUNTS = 0
        const val BUDGETS = 1
        const val CATEGORIES = 2
        const val CURRENCIES = 3
        const val MOVEMENTS = 4
        const val SETTLE_GROUPS = 5
    }

    val searchText: MutableLiveData<String> = savedStateHandle.getLiveData(SEARCH_TEXT_KEY, "")

    fun setSearText(text: String) {
        searchText.value = text
    }

    fun displayableItems(type: Int) = searchText.distinctUntilChanged().switchMap { searchText ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(
                selectionRepository.getSimpleQueryData(type, searchText).map { it.map { queryData -> queryData.forDisplay(type) } })
        }
    }

    private var items: LiveData<List<SimpleDisplayableData>>? = null

    fun get(type: Int, searchText: String): LiveData<List<SimpleDisplayableData>> {
        return items ?: liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(selectionRepository.getSimpleQueryData(type, searchText).map { it.map { queryData -> queryData.forDisplay(type) } })
        }.also { items = it }
    }

    val accounts = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(ACCOUNTS, query) }

    val budgets = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(BUDGETS, query) }

    val categories = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(CATEGORIES, query) }

    val movements = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(MOVEMENTS, query) }

    val settleGroups = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(SETTLE_GROUPS, query) }

    val accountId: MutableLiveData<String?> = savedStateHandle.getLiveData(ACCOUNT_ID_KEY, null)

    fun setAccountId(id: String) {
        accountId.value = id
    }

    val budgetId: MutableLiveData<String?> = savedStateHandle.getLiveData(BUDGET_ID_KEY, null)

    fun setBudgetId(id: String) {
        budgetId.value = id
    }

    val categoryId: MutableLiveData<String?> = savedStateHandle.getLiveData(CATEGORY_ID_KEY, null)

    fun setCategoryId(id: String) {
        categoryId.value = id
    }

    val movementId: MutableLiveData<String?> = savedStateHandle.getLiveData(MOVEMENT_ID_KEY, null)

    fun setMovementId(id: String) {
        movementId.value = id
    }

    val settleGroupId: MutableLiveData<String?> = savedStateHandle.getLiveData(SETTLE_GROUP_ID_KEY, null)

    fun setSettleGroupId(id: String) {
        settleGroupId.value = id
    }
}

data class Selection(val type: Int, var queryText: String = "")


/*private val selection =
        savedStateHandle.getLiveData("selection", Selection(SelectionViewModel.ACCOUNTS))

    fun setSelection(type: Int, queryText: String) {
        selection.value = Selection(type, queryText)
    }

    val t = selection.distinctUntilChanged().switchMap { selection ->
        liveData {
            emitSource(selectionRepository.getSimpleQueryData(selection.type, selection.queryText))
        }
    }*/

fun SimpleQueryData.forDisplay(type: Int): SimpleDisplayableData {
    return SimpleDisplayableData(
        id,
        name,
        when (type) {
            SelectionViewModel.ACCOUNTS -> Strings.get(typeId!!)
            SelectionViewModel.MOVEMENTS -> Strings.get(
                R.string.amount_holder,
                currencyCode!!,
                amount!!.forDisplayAmount(currencyCode!!)
            )
            else -> description
        },
        color,
        iconResId
    )
}