package com.ekosoftware.financialpreview.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Constants.CATEGORY_ADJUSTMENT_ID
import com.ekosoftware.financialpreview.app.Constants.CATEGORY_DEBT_LOAN_ID
import com.ekosoftware.financialpreview.app.Constants.CATEGORY_TRANSFER_ID
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.record.RecordUI
import com.ekosoftware.financialpreview.databinding.DetailsFragmentRecordBinding
import com.ekosoftware.financialpreview.util.applyMoneyFormat
import com.ekosoftware.financialpreview.util.forCommunicationAmount
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import java.text.SimpleDateFormat
import java.util.*

class DetailsRecordFragment : Fragment() {

    val args: DetailsRecordFragmentArgs by navArgs()

    private var _binding: DetailsFragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationIcon.setOnClickListener { findNavController().navigateUp() }
    }

    fun feedUI(record: RecordUI) = with(binding) {
        accountName.text = record.accountName
        accountColor.setBackgroundColor(
            Colors.get(
                record.accountColorResId ?: R.color.colorPrimary
            )
        )
        amount.applyMoneyFormat(record.currencyCode, record.amount.forCommunicationAmount())
        categoryName.text = record.categoryName
        record.categoryIconResId?.let { categoryIcon.setImageResource(it) }
        record.accountColorResId?.let { categoryColor.circleBackgroundColor = Colors.get(it) }
        record.description.let {
            if (it == null) info.hide()
            else info.text = it
        }
        record.date?.let {
            date.text = SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault()).format(it)
        }
        when (record.categoryId) {
            CATEGORY_ADJUSTMENT_ID -> null
            CATEGORY_TRANSFER_ID -> record.accountOutName?.let { Strings.get(R.string.transfer_from, it) }
            CATEGORY_DEBT_LOAN_ID -> when {
                record.debtorName != null -> {
                    Strings.get(R.string.you_ve_lend_to, record.debtorName!!)
                }
                record.lenderName != null -> {
                    Strings.get(R.string.debt_with, record.lenderName!!)
                }
                else -> null
            }
            else -> record.name
        }.let {
            if (it.isNullOrEmpty()) name.hide() else name.text = it
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}