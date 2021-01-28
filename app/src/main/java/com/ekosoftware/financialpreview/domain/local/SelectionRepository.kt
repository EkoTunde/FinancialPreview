package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.presentation.Selection
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import javax.inject.Inject

class SelectionRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao
) {

    fun getSimpleQueryData(selection: Selection): LiveData<List<SimpleQueryData>> {
        return when (selection.type) {
            Selection.Type.ACCOUNTS -> {
                getAccounts(selection.queryText)
            }
            Selection.Type.BUDGETS, Selection.Type.BUDGETS_SETTLE -> {
                getBudgets(selection.queryText)
            }
            Selection.Type.CATEGORIES -> {
                getCategories(selection.queryText)
            }
            Selection.Type.MOVEMENTS -> {
                getMovements(selection.queryText)
            }
            Selection.Type.SETTLE_GROUPS -> {
                getSettleGroups(selection.queryText)
            }
        }
    }

    fun getAccounts(searchPhrase: String): LiveData<List<SimpleQueryData>> {
        return accountDao.getAccountsAsSimpleData(searchPhrase)
    }

    fun getBudgets(searchPhrase: String): LiveData<List<SimpleQueryData>> {
        return budgetDao.getBudgetsAsSimpleData(searchPhrase)
    }

    fun getCategories(searchPhrase: String): LiveData<List<SimpleQueryData>> {
        return categoryDao.getCategoriesAsSimpleData(searchPhrase)
    }

    fun getMovements(searchPhrase: String): LiveData<List<SimpleQueryData>> {
        return movementDao.getMovementsAsSimpleData(searchPhrase)
    }

    fun getSettleGroups(searchPhrase: String): LiveData<List<SimpleQueryData>> {
        return settleGroupDao.getSettleGroupsAsSimpleData(searchPhrase)
    }
}