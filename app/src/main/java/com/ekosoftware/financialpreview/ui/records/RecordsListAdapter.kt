package com.ekosoftware.financialpreview.ui.records


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.record.RecordSummary
import com.ekosoftware.financialpreview.databinding.FragmentRecordsBinding
import com.ekosoftware.financialpreview.databinding.ItemRecordBinding

class RecordsListAdapter(
    private var onSelected: (RecordSummary) -> Unit
) :
    ListAdapter<RecordSummary, RecordsListAdapter.RecordsListViewHolder>(RecordDiffCallback()) {

    inner class RecordsListViewHolder(
        private val binding: ItemRecordBinding,
        private val onSelected: (RecordSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecordSummary) {

            binding.recordAmount.text =
                Strings.get(R.string.amount_holder, item.currencyCode, item.amount)

            binding.recordName.text = item.name

            if (item.accountIdIn == null) {
                item.categoryName?.let { binding.recordCategory.text = it }
                item.categoryColorResId?.let {
                    binding.recordColorCategory.setCircleBackgroundColorResource(
                        it
                    )
                }
                TODO("binding.recordIcon.setImageResource(item.categoryIconResId?)")
                TODO("Review")
                item.categoryIconResId?.let { binding.recordIcon.setImageResource(it) }
            }

            binding.recordDate.text = item.date.toString()

            binding.root.setOnClickListener {
                onSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsListViewHolder {
        return RecordsListViewHolder(
            ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onSelected
        )
    }

    override fun onBindViewHolder(holderList: RecordsListViewHolder, position: Int) {
        holderList.bind(getItem(position))
    }
}

class RecordDiffCallback : DiffUtil.ItemCallback<RecordSummary>() {
    override fun areItemsTheSame(oldItem: RecordSummary, newItem: RecordSummary): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: RecordSummary, newItem: RecordSummary): Boolean =
        oldItem == newItem
}
