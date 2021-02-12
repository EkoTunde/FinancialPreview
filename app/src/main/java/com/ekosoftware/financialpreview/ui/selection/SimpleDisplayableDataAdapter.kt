package com.ekosoftware.financialpreview.ui.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.SimpleDisplayableData
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.util.hide

class SimpleDisplayableDataAdapter(onSelected: (v: View, SimpleDisplayableData) -> Unit) :
    BaseListAdapter<SimpleDisplayableData, ItemSelectionExtendedBinding>(onSelected) {

    override fun bind(item: SimpleDisplayableData, binding: ItemSelectionExtendedBinding) =
        with(binding) {

            if (item.color != null) {
                colorIconContainer.setBackgroundColor(Colors.get(item.color!!))
            } else colorIconContainer.hide()

            item.iconResId?.let {
                colorIconContainer.setImageResource(item.iconResId!!)
            }

            name.text = item.name
            if (item.description.isNullOrEmpty()) {
                description.hide()
            } else description.text = item.description
        }

    override fun inflateBinding(parent: ViewGroup): ItemSelectionExtendedBinding {
        return ItemSelectionExtendedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}

