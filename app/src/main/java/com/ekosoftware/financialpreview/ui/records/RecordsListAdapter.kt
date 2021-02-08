package com.ekosoftware.financialpreview.ui.records


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.record.RecordUI
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
import com.ekosoftware.financialpreview.databinding.ItemRecordBinding

class RecordsListAdapter(private var onSelected: (v: View, RecordUIShort) -> Unit) :
    BaseListAdapter<RecordUIShort, ItemRecordBinding>(onSelected) {

    override fun bind(item: RecordUIShort, binding: ItemRecordBinding) = with(binding) {
        recordAmount.text = Strings.get(R.string.amount_holder, item.currencyCode, item.amount)
        recordName.text = item.name
        item.categoryName?.let { recordCategory.text = it }
        item.categoryColorResId?.let { recordColorCategory.setCircleBackgroundColorResource(it) }
        item.categoryIconResId?.let { recordIcon.setImageResource(it) }
        binding.recordDate.text = item.date.toString()
        binding.root.setOnClickListener {
            onSelected(it, item)
        }
    }

    override fun inflateBinding(parent: ViewGroup): ItemRecordBinding {
        return ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}
