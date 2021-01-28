package com.ekosoftware.financialpreview.ui.pending.tabs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import com.ekosoftware.financialpreview.databinding.ItemPendingMovementBinding
import com.ekosoftware.financialpreview.util.forCommunicationAmount
import com.ekosoftware.financialpreview.util.forDisplayAmount

class PendingMovementListAdapter(onSelected: (v: View, MovementSummary) -> Unit) :
    BaseListAdapter<MovementSummary, ItemPendingMovementBinding>(onSelected) {

    override fun bind(item: MovementSummary, binding: ItemPendingMovementBinding) = with(binding) {
        pendingName.text = item.name
        pendingCategory.text = item.categoryName
        pendingCategoryColor.circleBackgroundColor = item.categoryColorResId ?: R.color.colorPrimary
        pendingIcon.setImageResource(item.categoryIconResId ?: R.drawable.ic_movement)
        pendingAmount.text = Strings.get(R.string.amount_holder, item.currencyCode, item.leftAmount.forCommunicationAmount().forDisplayAmount(item.currencyCode))
    }

    override fun viewBindingClass(parent: ViewGroup): ItemPendingMovementBinding {
        return ItemPendingMovementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}