package com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.summary.MovementSummary
import com.ekosoftware.financialpreview.databinding.ItemPendingMovementBinding
import kotlinx.android.synthetic.main.item_pending_movement.view.*

class PendingMovementsRecyclerAdapter(
    private val context: Context,
    private val interaction: Interaction? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<MovementSummary>() {

        override fun areItemsTheSame(oldItem: MovementSummary, newItem: MovementSummary): Boolean {
            return oldItem.movementId == newItem.movementId
        }

        override fun areContentsTheSame(
            oldItem: MovementSummary,
            newItem: MovementSummary
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PendingMovementsViewHolder(
        private val binding: ItemPendingMovementBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovementSummary) {

            binding.pendingName.text = item.name

            binding.pendingCategory.text = item.categoryName

            binding.pendingCategoryColor.circleBackgroundColor =
                ResourcesCompat.getColor(context.resources, item.categoryColorResId, null)

            binding.pendingIcon.setImageResource(item.categoryIconResId)

            binding.pendingAmount.text = context.getString(R.string.amount_holder, item.currencyCode, item.leftAmount)

            itemView.apply {
                setOnClickListener {
                    interaction?.onItemSelected(item)
                }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: MovementSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingMovementsViewHolder {
        return PendingMovementsViewHolder(
            ItemPendingMovementBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PendingMovementsViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<MovementSummary>) {
        differ.submitList(list)
    }
}