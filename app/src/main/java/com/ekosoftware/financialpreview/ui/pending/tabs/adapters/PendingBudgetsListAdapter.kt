package com.ekosoftware.financialpreview.ui.pending.tabs.adapters


import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.ItemPendingBudgetBinding

class PendingBudgetsListAdapter(onSelected: (Budget) -> Unit) :
    BaseListAdapter<Budget, ItemPendingBudgetBinding>(onSelected) {

    override fun bind(item: Budget, binding: ItemPendingBudgetBinding) = with(binding) {
        val percentLeft = ((item.leftAmount * 100) / item.startingAmount).toInt()
        val percentUsed = 100 - percentLeft
        budgetPercent.text = Strings.get(R.string.budget_percent, percentUsed)

        budgetName.text = item.name

        val progressColor = Colors.progressColor(percentLeft)

        budgetProgress.progress = percentLeft
        budgetProgress.progressTintList = ColorStateList.valueOf(progressColor)

        budgetStartingAmount.text =
            Strings.get(R.string.amount_holder, item.currencyCode, item.startingAmount)

        budgetStartingAmount.setTextColor(
            Colors.get(
                if (item.leftAmount == item.startingAmount) {
                    R.color.colorAmountPositive
                } else {
                    R.color.colorLightGrey
                }
            )
        )
        budgetLeftAmount.text =
            Strings.get(R.string.amount_holder, item.currencyCode, item.leftAmount)
        budgetLeftAmount.setTextColor(progressColor)
    }

    override fun viewBindingClass(parent: ViewGroup): ItemPendingBudgetBinding {
        return ItemPendingBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}