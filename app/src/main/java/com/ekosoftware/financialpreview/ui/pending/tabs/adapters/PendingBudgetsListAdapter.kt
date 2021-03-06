package com.ekosoftware.financialpreview.ui.pending.tabs.adapters


import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.ItemPendingBudgetBinding
import com.ekosoftware.financialpreview.util.forCommunicationAmount
import com.ekosoftware.financialpreview.util.forDisplayAmount
import java.math.BigDecimal
import java.math.RoundingMode

class PendingBudgetsListAdapter(onSelected: (v: View, Budget) -> Unit) :
    BaseListAdapter<Budget, ItemPendingBudgetBinding>(onSelected) {

    override fun bind(item: Budget, binding: ItemPendingBudgetBinding) = with(binding) {

        this.root.transitionName = item.id

        val percentLeft = BigDecimal(item.leftAmount.toString()).times(BigDecimal("100.00")).div(BigDecimal(item.startingAmount.toString()))
            .setScale(2, RoundingMode.DOWN)
        budgetPercent.text = Strings.get(R.string.budget_percent, percentLeft, "%")

        budgetName.text = item.name

        val progressColor = Colors.progressColor(percentLeft.toInt())

        budgetProgress.progress = percentLeft.toInt()
        budgetProgress.progressTintList = ColorStateList.valueOf(progressColor)

        budgetStartingAmount.text =
            Strings.get(R.string.amount_holder, item.currencyCode, item.startingAmount.forDisplayAmount(item.currencyCode))

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
            Strings.get(R.string.amount_holder, item.currencyCode, item.leftAmount.forDisplayAmount(item.currencyCode))
        budgetLeftAmount.setTextColor(progressColor)
    }

    override fun inflateBinding(parent: ViewGroup): ItemPendingBudgetBinding {
        return ItemPendingBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}