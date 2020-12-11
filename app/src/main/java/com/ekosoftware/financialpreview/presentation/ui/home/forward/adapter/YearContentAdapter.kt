package com.ekosoftware.financialpreview.presentation.ui.home.forward.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.model.YearMonth

class YearContentAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<YearMonth>() {

        override fun areItemsTheSame(oldItem: YearMonth, newItem: YearMonth): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: YearMonth, newItem: YearMonth): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class YearContentViewHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: YearMonth) {
            itemView.apply {
                setOnClickListener {
                    interaction?.onItemSelected(item)
                }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: YearMonth)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forward_content, parent, false)
        return YearContentViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YearContentViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<YearMonth>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }
}