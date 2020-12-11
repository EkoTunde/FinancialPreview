package com.ekosoftware.financialpreview.presentation.ui.home.forward.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R

class HeaderTitleAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var title: String

    fun submitTitle(title: String) {
        this.title = title
        notifyDataSetChanged()
    }

    inner class YearHeaderTitleViewHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String) {
            itemView.apply {
                setOnClickListener {
                    interaction?.onItemSelected(item)
                }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearHeaderTitleViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_forward_header, parent, false)
        return YearHeaderTitleViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YearHeaderTitleViewHolder -> {
                holder.bind(title)
            }
        }
    }

    override fun getItemCount(): Int = 1
}