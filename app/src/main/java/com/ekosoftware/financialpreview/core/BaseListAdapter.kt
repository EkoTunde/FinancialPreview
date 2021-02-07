package com.ekosoftware.financialpreview.core

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.View


/**
 * Base class of [ListAdapter] that provides the basic implementation to bind
 * [View]s in a [RecyclerView]. It gives subclasses the ability to avoid boilerplate code.
 *
 * @param T data class item which properties need to be bind to [View]s
 * @param K [ViewBinding] reference to an xml.
 * @param onSelected code performed when and item's layout is clicked.
 *
 */
abstract class BaseListAdapter<T, K : ViewBinding>(
    private val onSelected: ((v: View, T) -> Unit)?
) :
    ListAdapter<T, BaseListAdapter<T, K>.BaseListViewHolder>(BaseDiffCallback<T>()) {

    inner class BaseListViewHolder(
        private val binding: K,
        private val onSelected: ((v: View, T) -> Unit)?
    ) : BaseViewHolder<T>(binding.root) {
        override fun bind(item: T) {
            binding.root.setOnClickListener {
                onSelected?.let { it1 -> it1(it, item) }
            }
            this@BaseListAdapter.bind(item, binding)
        }
    }

    /**
     * Called from [BaseListViewHolder] when binding views.
     * Gives subclasses the place for binding views to specific xml which reference is held by [binding].
     *
     * @param item [T] object represents a data class or POJO.
     * @param binding represents a [ViewBinding] subclass.
     */
    protected abstract fun bind(item: T, binding: K)

    /**
     * Called to provide the [ViewBinding] class holding a reference to item's xml.
     */
    protected abstract fun inflateBinding(parent: ViewGroup): K

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListViewHolder {
        return BaseListViewHolder(inflateBinding(parent), onSelected)
    }

    override fun onBindViewHolder(holderList: BaseListViewHolder, position: Int) {
        holderList.bind(getItem(position))
    }
}
