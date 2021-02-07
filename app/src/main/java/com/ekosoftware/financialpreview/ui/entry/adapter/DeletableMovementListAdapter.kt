package com.ekosoftware.financialpreview.ui.entry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.databinding.ItemEntrySettleGroupMovementsBinding

class DeletableMovementListAdapter(
    private val onRemoveMovement: (movementId: String) -> Unit
) :
    BaseListAdapter<Movement, ItemEntrySettleGroupMovementsBinding>(null) {

    override fun bind(item: Movement, binding: ItemEntrySettleGroupMovementsBinding) {
        binding.removeBtn.setOnClickListener { onRemoveMovement(item.id) }
        binding.name.text = item.name
    }

    override fun inflateBinding(parent: ViewGroup): ItemEntrySettleGroupMovementsBinding {
        return ItemEntrySettleGroupMovementsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}