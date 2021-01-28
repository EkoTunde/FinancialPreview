package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) {

    fun getCategories(queryText: String): LiveData<List<SimpleQueryData>> {
        return categoryDao.getCategoriesAsSimpleData(queryText)
    }
}