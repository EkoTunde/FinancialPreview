package com.ekosoftware.financialpreview.presentation.ui.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.databinding.FragmentPendingBinding
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.PendingPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class PendingFragment : Fragment() {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PendingPagerAdapter

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
    }

    private fun setUpAdapter() {
        adapter = PendingPagerAdapter(requireActivity())

        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position + 1) {
                1 -> {
                    tab.text = getString(R.string.movements)
                }
                2 -> {
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