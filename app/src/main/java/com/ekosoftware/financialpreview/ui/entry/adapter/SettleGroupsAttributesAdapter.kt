package com.ekosoftware.financialpreview.ui.entry.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.databinding.ItemEntrySettleGroupAttributesBinding
import java.lang.Exception

class SettleGroupsAttributesAdapter(private val onAttributesListener: OnAttributesListener) :
    RecyclerView.Adapter<SettleGroupsAttributesAdapter.SettleGroupsAttributesViewHolder>() {

    private var settleGroup: SettleGroup? = null
    private var colorResId: Int? = null

    fun updateAttrs(settleGroup: SettleGroup) {
        this.settleGroup = settleGroup
        updateColor(settleGroup.colorResId)
        notifyDataSetChanged()
    }

    fun updateColor(newColorResId: Int) {
        colorResId = newColorResId
        notifyDataSetChanged()
    }

    inner class SettleGroupsAttributesViewHolder(
        private val binding: ItemEntrySettleGroupAttributesBinding,
        private val onAttributesListener: OnAttributesListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettleGroup?, colorResId: Int?) {
            with(binding) {
                item?.let {
                    name.editText?.setText(it.name)
                    description.editText?.setText(it.description)
                    taxPercentage.editText?.setText(it.percentage.toString())
                }
                try { colorResId?.let { colorContainer.setBackgroundColor(Colors.get(it)) } } catch (e: Exception) {
                    colorContainer.setBackgroundColor(Colors.get(R.color.colorPrimary))
                }
                name.editText?.doOnTextChanged { text, _, _, _ -> onAttributesListener.onNameChanged(text.toString()) }
                name.editText?.clearFocus()
                description.editText?.doOnTextChanged { text, _, _, _ -> onAttributesListener.onDescriptionChange(text.toString()) }
                description.editText?.clearFocus()
                taxPercentage.editText?.doOnTextChanged { text, _, _, _ ->
                    if (text.toString().isEmpty()) {
                        onAttributesListener.onPercentageChanged(0.0)
                    } else {
                        onAttributesListener.onPercentageChanged(text.toString().toDouble())
                    }
                }
                taxPercentage.editText?.clearFocus()
                btnChangeColor.setOnClickListener { onAttributesListener.onChangeColor() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettleGroupsAttributesViewHolder {
        return SettleGroupsAttributesViewHolder(
            ItemEntrySettleGroupAttributesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onAttributesListener
        )
    }


    override fun onBindViewHolder(holderAttributes: SettleGroupsAttributesViewHolder, position: Int) {
        holderAttributes.bind(settleGroup, colorResId)
    }

    override fun getItemCount(): Int = 1

    interface OnAttributesListener {
        fun onNameChanged(name: String)
        fun onDescriptionChange(description: String)
        fun onPercentageChanged(percentage: Double)
        fun onChangeColor()
    }
}