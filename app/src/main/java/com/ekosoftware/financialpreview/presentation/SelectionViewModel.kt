package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.domain.local.SelectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers

class SelectionViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val selectionRepository: SelectionRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val ACCOUNTS_SEARCH_STRING_KEY = "accountsSearchStringKey"
    }

    private val selection =
        savedStateHandle.getLiveData("selection", Selection(Selection.Type.ACCOUNTS))

    fun setSelection(type: Selection.Type, queryText: String) {
        selection.value = Selection(type, queryText)
    }

    val extendedItems = selection.distinctUntilChanged().switchToResourceIOLiveData {
        selectionRepository.getSimpleQueryData(it)
    }
}

