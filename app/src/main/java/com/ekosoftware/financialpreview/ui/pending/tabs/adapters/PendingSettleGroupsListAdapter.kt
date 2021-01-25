package com.ekosoftware.financialpreview.ui.pending.tabs.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.ItemPendingSettleGroupBinding

class PendingSettleGroupsListAdapter(onSelected: (SettleGroupWithMovements) -> Unit) :
    BaseListAdapter<SettleGroupWithMovements, ItemPendingSettleGroupBinding>(onSelected) {

    override fun bind(item: SettleGroupWithMovements, binding: ItemPendingSettleGroupBinding) =
        with(binding) {
            settleGroupName.text = item.settleGroup.name

            settleGroupPercent.text =
                if (item.settleGroup.percentage > 0.0) Strings.get(
                    R.string.settle_group_percent,
                    item.settleGroup.percentage
                ) else Strings.get(R.string.settle_group_percent_0)

            settleGroupMovementsCount.text = Strings.getQuantity(
                R.plurals.amountOfMovementsInSettleGroup,
                item.movements.size
            )
        }

    override fun viewBindingClass(parent: ViewGroup): ItemPendingSettleGroupBinding {
        return ItemPendingSettleGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}