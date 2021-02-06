package com.ekosoftware.financialpreview.ui.accounts


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.databinding.ItemAccountBinding
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.util.forDisplayAmount

class AccountsListAdapter(private val onSelected: (v: View, Account) -> Unit) :
    BaseListAdapter<Account, ItemAccountBinding>(onSelected) {
    override fun bind(item: Account, binding: ItemAccountBinding) = with(binding) {
        name.text = item.name
        amountTxt.text =
            binding.root.context.getString(
                R.string.amount_holder,
                item.currencyCode,
                item.balance.forDisplayAmount(item.currencyCode)
            )
    }

    override fun inflateBinding(parent: ViewGroup): ItemAccountBinding {
        return ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}