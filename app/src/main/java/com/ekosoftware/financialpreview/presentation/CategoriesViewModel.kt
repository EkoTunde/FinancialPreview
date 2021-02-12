package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.ekosoftware.financialpreview.core.BaseViewModel
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import com.ekosoftware.financialpreview.repository.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val savedStateHandle: SavedStateHandle
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