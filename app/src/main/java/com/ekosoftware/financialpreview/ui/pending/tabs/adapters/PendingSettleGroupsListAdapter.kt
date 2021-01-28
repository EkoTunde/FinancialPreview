package com.ekosoftware.financialpreview.ui.pending.tabs.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.ItemPendingSettleGroupBinding

class PendingSettleGroupsListAdapter(onSelected: (v: View, SettleGroupWithMovements) -> Unit) :
    BaseListAdapter<SettleGroupWithMovements, ItemPendingSettleGroupBinding>(onSelected) {

    override fun bind(item: SettleGroupWithMovements, binding: ItemPendingSettleGroupBinding) =
        with(binding) {
            settleGroupName.text = item.settleGroup.name

            settleGroupPercent.text =
                    //Strings.getQuantity(R.plurals.amountOfSettleGroupPercent, 0, item.settleGroup.percentage)
                if (item.settleGroup.percentage > 0.0) Strings.get(
                    R.string.settle_group_percent,
                    item.settleGroup.percentage.toString().removeZeros(), "%"
                ) else Strings.get(R.string.settle_group_percent_0)

            settleGroupPercent.setTextColor(Colors.get(if (item.settleGroup.percentage > 0.0) R.color.colorAmountNegative else R.color.colorAmountPositive))

            settleGroupMovementsCount.text = Strings.getQuantity(
                R.plurals.amountOfMovementsInSettleGroup,
                item.movements.size, item.movements.size
            )
        }

    override fun viewBindingClass(parent: ViewGroup): ItemPendingSettleGroupBinding {
        return ItemPendingSettleGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    fun String.removeZeros(): String {
        return if (this.contains(".") && (this.endsWith("0") || this.endsWith("."))) {
            this.substring(0, this.length - 1).removeZeros()
        } else {
            this
        }
    }
}