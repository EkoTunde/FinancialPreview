package com.ekosoftware.financialpreview.repository

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.CategoryDao
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) {

    fun getCategories(queryText: String): LiveData<List<SimpleQueryData>> {
        return categoryDao.getCategoriesAsSimpleData(queryText)
    }
}