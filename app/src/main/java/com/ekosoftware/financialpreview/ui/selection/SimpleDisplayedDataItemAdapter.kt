package com.ekosoftware.financialpreview.ui.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.presentation.SimpleDisplayableData
import com.ekosoftware.financialpreview.util.hide

class SimpleDisplayedDataItemAdapter<T : Any>(onSelected: (v: View, SimpleDisplayableData) -> Unit) :
    BaseListAdapter<SimpleDisplayableData, ItemSelectionExtendedBinding>(onSelected) {

    override fun bind(item: SimpleDisplayableData, binding: ItemSelectionExtendedBinding) =
        with(binding) {

            if (item.color != null) { colorIconContainer.setBackgroundColor(item.color!!) } else colorIconContainer.hide()

            item.iconResId?.let {
                colorIconContainer.setImageResource(item.iconResId!!)
            }

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

