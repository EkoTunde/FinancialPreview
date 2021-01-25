package com.ekosoftware.financialpreview.ui.records


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.record.RecordSummary
import com.ekosoftware.financialpreview.databinding.FragmentRecordsBinding
import com.ekosoftware.financialpreview.databinding.ItemRecordBinding

class RecordsListAdapter(private var onSelected: (RecordSummary) -> Unit) :
    BaseListAdapter<RecordSummary, ItemRecordBinding>(onSelected) {

    override fun bind(item: RecordSummary, binding: ItemRecordBinding) = with(binding) {
        recordAmount.text = Strings.get(R.string.amount_holder, item.currencyCode, item.amount)
        recordName.text = item.name
        if (item.accountIdIn == null) {
            item.categoryName?.let { recordCategory.text = it }
            item.categoryColorResId?.let {
                recordColorCategory.setCircleBackgroundColorResource(
                    it
                )
            }
            TODO("binding.recordIcon.setImageResource(item.categoryIconResId?)")
            TODO("Review")
            item.categoryIconResId?.let { recordIcon.setImageResource(it) }
        }

        binding.recordDate.text = item.date.toString()

        binding.root.setOnClickListener {
            onSelected(item)
        }
    }

    override fun viewBindingClass(parent: ViewGroup): ItemRecordBinding {
        return ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}
