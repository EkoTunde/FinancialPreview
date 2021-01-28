package com.ekosoftware.financialpreview.ui.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.presentation.SimpleDisplayedData

class SimpleDisplayedDataItemAdapter<T : Any>(onSelected: (v: View, SimpleDisplayedData) -> Unit) :
    BaseListAdapter<SimpleDisplayedData, ItemSelectionExtendedBinding>(onSelected) {

    override fun bind(item: SimpleDisplayedData, binding: ItemSelectionExtendedBinding) =
        with(binding) {
            item.iconResId?.let { colorIconContainer.setImageResource(it) }
            item.color?.let { colorIconContainer.setBackgroundColor(it) }
            name.text = item.name
            description.text = item.description ?: ""
        }

    override fun viewBindingClass(parent: ViewGroup): ItemSelectionExtendedBinding {
        return ItemSelectionExtendedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}

