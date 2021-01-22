package com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters


import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.local.ColorsResourceInts
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.ItemPendingBudgetBinding

class PendingBudgetsRecyclerAdapter(
    private val context: Context,
    private val interaction: Interaction? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Budget>() {

        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.budgetUUID == newItem.budgetUUID
        }

        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PendingMovementsViewHolder(
        private val binding: ItemPendingBudgetBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Budget) {

            val percentLeft = ((item.leftAmount * 100) / item.startingAmount).toInt()
            val percentUsed = 100 - percentLeft
            binding.budgetPercent.text = context.getString(R.string.budget_percent, percentUsed)

            binding.budgetName.text = item.name

            val progressColor = ColorsResourceInts.getColorForProgress(percentLeft)

            binding.budgetProgress.progress = percentLeft
            binding.budgetProgress.progressTintList = ColorStateList.valueOf(progressColor)

            binding.budgetStartingAmount.text =
                context.getString(R.string.amount_holder, item.currencyCode, item.startingAmount)

            binding.budgetStartingAmount.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (item.leftAmount == item.startingAmount) {
                        R.color.colorAmountPositive
                    } else {
                        R.color.colorLightGrey
                    }
                )
            )
            binding.budgetLeftAmount.text =
                context.getString(R.string.amount_holder, item.currencyCode, item.leftAmount)
            binding.budgetLeftAmount.setTextColor(ContextCompat.getColor(context, progressColor))

            itemView.apply {
                setOnClickListener {
                    interaction?.onItemSelected(item)
                }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: Budget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingMovementsViewHolder {
        return PendingMovementsViewHolder(
            ItemPendingBudgetBinding.inflate(
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

    fun submitList(list: List<Budget>) {
        differ.submitList(list)
    }
}