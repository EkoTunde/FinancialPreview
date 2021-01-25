package com.ekosoftware.financialpreview.ui.accounts


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.databinding.ItemAccountBinding

class AccountsListAdapter(private val onSelected: (Account) -> Unit) :
    BaseListAdapter<Account, ItemAccountBinding>(onSelected) {
    override fun bind(item: Account, binding: ItemAccountBinding) = with(binding) {
        name.text = item.name
        amountBtn.text =
            binding.root.context.getString(
                R.string.amount_holder,
                item.currencyCode,
                item.balance
            )
        amountBtn.setOnClickListener {
            onSelected(item)
        }
    }

    override fun viewBindingClass(parent: ViewGroup): ItemAccountBinding {
        return ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}