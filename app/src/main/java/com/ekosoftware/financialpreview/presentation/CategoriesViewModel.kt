package com.ekosoftware.financialpreview.presentation

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.domain.local.CategoriesRepository

class CategoriesViewModel @ViewModelInject constructor(
    private val categoriesRepository: CategoriesRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val queryText = savedStateHandle.getLiveData("CategoriesSearchText", "")

    fun searchWith(queryText: String) {
        this.queryText.value = queryText
    }

    val categories: LiveData<Resource<List<SimpleQueryData>>> =
        queryText.switchToResourceIOLiveData { text ->
            categoriesRepository.getCategories(text)
        }
}