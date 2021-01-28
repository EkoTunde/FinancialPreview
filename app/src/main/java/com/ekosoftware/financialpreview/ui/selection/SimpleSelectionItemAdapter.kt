package com.ekosoftware.financialpreview.ui.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.databinding.ItemSelectionSimpleBinding

class SimpleSelectionItemAdapter<T : Any>(onSelected: (v: View, SimpleItem<T>) -> Unit) :
    BaseListAdapter<SimpleItem<T>, ItemSelectionSimpleBinding>(onSelected) {

    override fun bind(item: SimpleItem<T>, binding: ItemSelectionSimpleBinding) = with(binding) {
        name.text = item.name
        description.text = item.description
    }

    override fun viewBindingClass(parent: ViewGroup): ItemSelectionSimpleBinding {
        return ItemSelectionSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}

data class SimpleItem<T : Any>(var id: T, var name: String, var description: String?)