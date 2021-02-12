package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.SimpleDisplayableData
import com.ekosoftware.financialpreview.repository.SelectionRepository
import com.ekosoftware.financialpreview.util.forDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val selectionRepository: SelectionRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val SELECTION_SEARCH_TEXT_KEY = "accountsSearchStringKey"
        private const val SELECTION_ACCOUNT_ID_KEY = "SelectionAccountIdKey"
        private const val SELECTION_BUDGET_ID_KEY = "SelectionBudgetIdKey"
        private const val SELECTION_CATEGORY_ID_KEY = "SelectionCategoryIdKey"
        private const val SELECTION_CURRENCY_ID_KEY = "SelectionCurrencyIdKey"
        private const val SELECTION_MOVEMENT_ID_KEY = "SelectionMovementIdKey"
        private const val SELECTION_SETTLE_GROUP_ID_KEY = "SelectionSettleGroupIdKey"

        const val ACCOUNTS = 0
        const val BUDGETS = 1
        const val CATEGORIES = 2
        const val CURRENCIES = 3
        const val MOVEMENTS = 4
        const val SETTLE_GROUPS = 5
        const val SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS = 6

        private const val TAG = "SelectionViewModel"
    }

    private val searchText: MutableLiveData<String> = savedStateHandle.getLiveData(SELECTION_SEARCH_TEXT_KEY, "")

    fun setSearchText(text: String) {
        searchText.value = text
    }

    private val items: LiveData<Resource<List<SimpleDisplayableData>>>? = null

    fun get(type: Int = ACCOUNTS, genericId: String? = null): LiveData<Resource<List<SimpleDisplayableData>>> =
        items ?: searchText.distinctUntilChanged().switchMap { searchText ->
            liveData<Resource<List<SimpleDisplayableData>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(Resource.Loading())
                try {
                    emitSource(
                        selectionRepository.getSimpleQueryData(type, searchText, genericId).map {
                            it.map { queryData -> queryData.forDisplay(type) }
                        }.map {
                            Resource.Success(it)
                        }
                    )
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }

    val accounts = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(ACCOUNTS, query) }

    val budgets = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(BUDGETS, query) }

    val categories = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(CATEGORIES, query) }

    val currencies = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(CURRENCIES, query) }

    val movements = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(MOVEMENTS, query) }

    val settleGroups = searchText.distinctUntilChanged().switchMap { query -> selectionRepository.getSimpleQueryData(SETTLE_GROUPS, query) }

    val settleGroupsToAddToMovements = searchText.distinctUntilChanged().switchMap { query ->
        selectionRepository.getSimpleQueryData(
            SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS, query
        )
    }

    val accountId: MutableLiveData<String?> = savedStateHandle.getLiveData(SELECTION_ACCOUNT_ID_KEY, null)

    fun setAccountId(id: String?) {
        savedStateHandle[SELECTION_ACCOUNT_ID_KEY] = id
    }

    val budgetId: MutableLiveData<String?> = savedStateHandle.getLiveData(SELECTION_BUDGET_ID_KEY, null)

    fun setBudgetId(id: String?) {
        budgetId.value = id
    }

    val categoryId: MutableLiveData<String?> = savedStateHandle.getLiveData(SELECTION_CATEGORY_ID_KEY, null)

    fun setCategoryId(id: String?) {
        savedStateHandle[SELECTION_CATEGORY_ID_KEY] = id
    }

    val currencyId: MutableLiveData<String?> = savedStateHandle.getLiveData(SELECTION_CURRENCY_ID_KEY, null)

    fun setCurrencyId(id: String?) {
        savedStateHandle[SELECTION_CURRENCY_ID_KEY] = id
    }

    val movementId: MutableLiveData<String?> = savedStateHandle.getLiveData(SELECTION_MOVEMENT_ID_KEY, null)

    fun setMovementId(id: String?) {
        savedStateHandle[SELECTION_MOVEMENT_ID_KEY] = id
    }

    private var _settleGroupId: LiveData<String?>? = null

    val settleGroupId: MutableLiveData<String?> = savedStateHandle.getLiveData(SELECTION_SETTLE_GROUP_ID_KEY, null)

    fun setSettleGroupId(id: String?) {
        savedStateHandle[SELECTION_SETTLE_GROUP_ID_KEY] = id
    }

    fun getSettleGroupId(): LiveData<String?> = _settleGroupId ?: liveData<String?> {
        emitSource(savedStateHandle.getLiveData(SELECTION_SETTLE_GROUP_ID_KEY, null))
    }.also {
        _settleGroupId = it
    }

    override fun onCleared() {
        super.onCleared()
        setSearchText("")
        savedStateHandle[SELECTION_ACCOUNT_ID_KEY] = null
        savedStateHandle[SELECTION_BUDGET_ID_KEY] = null
        savedStateHandle[SELECTION_CATEGORY_ID_KEY] = null
        savedStateHandle[SELECTION_CURRENCY_ID_KEY] = null
        savedStateHandle[SELECTION_MOVEMENT_ID_KEY] = null
        savedStateHandle[SELECTION_SETTLE_GROUP_ID_KEY] = null
    }

    fun clearData() {
        savedStateHandle[SELECTION_ACCOUNT_ID_KEY] = null
        savedStateHandle[SELECTION_BUDGET_ID_KEY] = null
        savedStateHandle[SELECTION_CATEGORY_ID_KEY] = null
        savedStateHandle[SELECTION_CURRENCY_ID_KEY] = null
        savedStateHandle[SELECTION_MOVEMENT_ID_KEY] = null
        savedStateHandle[SELECTION_SETTLE_GROUP_ID_KEY] = null
    }
}