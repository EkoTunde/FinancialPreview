package com.ekosoftware.financialpreview.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.app.Constants.SETTLE_TYPE_GROUP
import com.ekosoftware.financialpreview.app.Constants.SETTLE_TYPE_MOVEMENT
import com.ekosoftware.financialpreview.app.Constants.SETTLE_TYPE_SIMPLE_RECORD
import com.ekosoftware.financialpreview.databinding.DialogBottomSettleOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettleOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogBottomSettleOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBottomSettleOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() = with(binding) {
        movementOption.setOnClickListener {
            val action = MainNavGraphDirections.actionGlobalSelectFragment(SETTLE_TYPE_MOVEMENT)
            findNavController().navigate(action)
        }
        groupOption.setOnClickListener {
            val action = MainNavGraphDirections.actionGlobalSelectFragment(SETTLE_TYPE_GROUP)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}