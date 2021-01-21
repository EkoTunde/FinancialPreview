package com.ekosoftware.financialpreview.core

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView


class HorizontalItemDecoration(
    color: Int,
    private val heightInPixels: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        paint.color = color
        paint.isAntiAlias = true
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + heightInPixels
            val adapterPosition = parent.getChildAdapterPosition(child)
            val viewType = parent.adapter?.getItemViewType(adapterPosition)
            if (viewType == RowType.ITEM.ordinal) { // here you make check before drawing the divider where row type determine if this item is header or normal item
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }
    }
}

enum class RowType {
    ITEM,
    HEADER;
}


//val dividerHeightInPixels = resources.getDimensionPixelSize(R.dimen.1sdp)
//val dividerItemDecoration = HorizontalItemDecoration(ContextCompat.getColor(requireContext(), R.color.divider_color), dividerHeightInPixels)
//recyclerView.addItemDecoration(dividerItemDecoration)

