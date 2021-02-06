package com.ekosoftware.financialpreview.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding

class CategoriesListAdapter(private val onSelected: (v: View, Category) -> Unit) :
    BaseListAdapter<Category, ItemSelectionExtendedBinding>(onSelected) {

    override fun bind(item: Category, binding: ItemSelectionExtendedBinding) = with(binding) {

    }

    override fun inflateBinding(parent: ViewGroup): ItemSelectionExtendedBinding {
        return ItemSelectionExtendedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}