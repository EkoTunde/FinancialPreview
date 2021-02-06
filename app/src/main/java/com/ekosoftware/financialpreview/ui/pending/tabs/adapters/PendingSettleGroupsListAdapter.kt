package com.ekosoftware.financialpreview.ui.pending.tabs.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovementsCount
import com.ekosoftware.financialpreview.databinding.ItemPendingSettleGroupBinding
import com.ekosoftware.financialpreview.util.removeZeros

class PendingSettleGroupsListAdapter(onSelected: (v: View, SettleGroupWithMovementsCount) -> Unit) :
    BaseListAdapter<SettleGroupWithMovementsCount, ItemPendingSettleGroupBinding>(onSelected) {

    override fun bind(item: SettleGroupWithMovementsCount, binding: ItemPendingSettleGroupBinding) =
        with(binding) {
            this.root.transitionName = item.id
            settleGroupName.text = item.name
            settleGroupPercent.text = if (item.percentage > 0.0) Strings.get(
                R.string.settle_group_percent,
                item.percentage.toString().removeZeros(), "%"
            ) else Strings.get(R.string.settle_group_percent_0)

            settleGroupPercent.setTextColor(Colors.get(if (item.percentage > 0.0) R.color.colorAmountNegative else R.color.colorAmountPositive))

            settleGroupMovementsCount.text = Strings.getQuantity(
                R.plurals.amountOfMovementsInSettleGroup,
                item.count ?: 0, item.count ?: 0
            )
        }

    override fun inflateBinding(parent: ViewGroup): ItemPendingSettleGroupBinding {
        return ItemPendingSettleGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}