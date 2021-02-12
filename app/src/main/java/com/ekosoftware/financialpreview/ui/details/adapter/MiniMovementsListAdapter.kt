package com.ekosoftware.financialpreview.ui.details.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.SimpleDisplayableData
import com.ekosoftware.financialpreview.databinding.ItemSelectionMiniBinding
import com.ekosoftware.financialpreview.util.hide

class MiniMovementsListAdapter(
    onSelected: (v: View, movement: SimpleDisplayableData) -> Unit
) :
    BaseListAdapter<SimpleDisplayableData, ItemSelectionMiniBinding>(onSelected) {

    override fun bind(item: SimpleDisplayableData, binding: ItemSelectionMiniBinding) {
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
    }

    override fun inflateBinding(parent: ViewGroup): ItemSelectionMiniBinding {
        return ItemSelectionMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}
