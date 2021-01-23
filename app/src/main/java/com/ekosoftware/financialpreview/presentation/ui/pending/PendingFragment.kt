package com.ekosoftware.financialpreview.presentation.ui.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.databinding.FragmentPendingBinding
import com.ekosoftware.financialpreview.presentation.ui.ShareViewModel
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters.PendingPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class PendingFragment : Fragment() {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel by activityViewModels<ShareViewModel>()
    private lateinit var adapter: PendingPagerAdapter

    private var currentPosition = 0

    companion object {
        private const val OPTION_ADD_MOVEMENT = 0
        private const val OPTION_ADD_BUDGET = 1
        private const val OPTION_ADD_SETTLE_GROUP = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        binding.btnAdd.setOnClickListener {
            val action = when (currentPosition) {
                OPTION_ADD_MOVEMENT -> PendingFragmentDirections.actionPendingPageToEditMovement(-1)
                OPTION_ADD_BUDGET -> PendingFragmentDirections.actionPendingPageToEditBudget(-1)
                OPTION_ADD_SETTLE_GROUP -> PendingFragmentDirections.actionPendingPageToEditGroup(null)
                else -> throw IllegalArgumentException("Position in tab mus be 0, 1 or 2. $currentPosition is not allowed.")
            }
            findNavController().navigate(action)
        }
    }

    private fun setUpAdapter() {
        adapter = PendingPagerAdapter(requireActivity(), object : PendingPagerAdapter.Interaction {
            override fun onTabSelected(position: Int) {
                currentPosition = position
            }
        })

        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            currentPosition = position
            when (position + 1) {
                1 -> {
                    currentPosition = position
                    tab.text = getString(R.string.movements)
                }
                2 -> {
                    tab.text = getString(R.string.budgets)
                }
                3 -> {
                    tab.text = getString(R.string.groups)
                }
            }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}