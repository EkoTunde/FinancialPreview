package com.ekosoftware.financialpreview.ui.settle

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.model.budget.Budget

class RecordFromBudgetSettleFragment : BaseSettleFragment<Budget>() {
    override val type: Int
        get() = TODO("Not yet implemented")

    override fun fetchData(): LiveData<Budget>? {
        return super.fetchData()
    }

    override fun setData(obj: Budget) {
        super.setData(obj)
    }

    override fun onSave() {
        super.onSave()
    }

    override fun setSelectionResultsListener() {
        super.setSelectionResultsListener()
    }

    override fun launchSelectionListener() {
        super.launchSelectionListener()
    }
}