package com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.ItemPendingSettleGroupBinding
import kotlinx.android.synthetic.main.item_pending_settle_group.view.*

class PendingSettleGroupsAdapter(
    private val context: Context,
    private val interaction: Interaction? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<SettleGroupWithMovements>() {

        override fun areItemsTheSame(
            oldItem: SettleGroupWithMovements,
            newItem: SettleGroupWithMovements
        ): Boolean {
            return oldItem.settleGroup.id == newItem.settleGroup.id
        }

        override fun areContentsTheSame(
            oldItem: SettleGroupWithMovements,
            newItem: SettleGroupWithMovements
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PendingSettleGroupsViewHolder(
        private val binding: ItemPendingSettleGroupBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettleGroupWithMovements) {

            binding.settleGroupName.text = item.settleGroup.name

            binding.settleGroupPercent.text =
                if (item.settleGroup.percentage > 0.0) context.getString(
                    R.string.settle_group_percent,
                    item.settleGroup.percentage
                ) else context.getString(R.string.settle_group_percent_0)

            binding.settleGroupMovementsCount.text = context.resources.getQuantityString(
                R.plurals.amountOfMovementsInSettleGroup,
                item.movements.size
            )

            itemView.apply {
                setOnClickListener {
                    interaction?.onItemSelected(item)
                }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: SettleGroupWithMovements)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingSettleGroupsViewHolder {
        return PendingSettleGroupsViewHolder(
            ItemPendingSettleGroupBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ), interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PendingSettleGroupsViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<SettleGroupWithMovements>) {
        differ.submitList(list)
    }
}