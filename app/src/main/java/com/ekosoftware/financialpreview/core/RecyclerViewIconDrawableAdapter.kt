package com.ekosoftware.financialpreview.core


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.data.local.CategoriesResourceInts
import kotlinx.android.synthetic.main.item_icon_drawable.view.*

class RecyclerViewIconDrawableAdapter(private val context: Context, private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerViewIconDrawableAdapter.IconDrawableAdapterViewHolder>() {

    private var itemList = CategoriesResourceInts.categoriesIntIds

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconDrawableAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon_drawable, parent, false)
        return IconDrawableAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = if (itemList.isNotEmpty()) itemList.size else 0

    override fun onBindViewHolder(holder: IconDrawableAdapterViewHolder, position: Int) {
        val item = itemList[position]
        holder.bindView(item)
    }

    inner class IconDrawableAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(item: Int) {
            itemView.image_holder.setImageDrawable(ResourcesCompat.getDrawable(context.resources, item, null))
            itemView.setOnClickListener {
                interaction?.onItemSelected(item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: Int)
    }
}