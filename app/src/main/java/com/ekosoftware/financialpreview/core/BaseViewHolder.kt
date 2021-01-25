package com.ekosoftware.financialpreview.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Provides a parent class to be inherited from which contains a simple [RecyclerView.ViewHolder] implementation.
 */
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}