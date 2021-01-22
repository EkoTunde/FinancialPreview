package com.ekosoftware.financialpreview.core

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.ekosoftware.financialpreview.R

class ImageAdapter(private val context: Context, private val gridViewItems: MutableList<Int>) : BaseAdapter() {

    override fun getCount(): Int = gridViewItems.size

    override fun getItem(position: Int): Int = gridViewItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        (convertView ?: (context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.item_dialog_icon_selection,
            parent,
            false
        )).apply {
            val imgIcon = findViewById<View>(R.id.icon_item) as ImageView
            imgIcon.setImageResource(gridViewItems[position])
        }
}