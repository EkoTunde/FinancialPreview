package com.ekosoftware.financialpreview.presentation.ui.pending.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.FragmentPendingSettleGroupsBinding
import com.ekosoftware.financialpreview.presentation.ui.MainViewModel
import com.ekosoftware.financialpreview.presentation.ui.ShareViewModel
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters.PendingSettleGroupsRecyclerAdapter
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show

class PendingSettleGroupsFragment : Fragment() {

    private var _binding: FragmentPendingSettleGroupsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val shareViewModel by activityViewModels<ShareViewModel>()

    private lateinit var rvAdapter: PendingSettleGroupsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingSettleGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        fetchData()
    }

    private fun setUI() = binding.rvPending.apply {
        layoutManager = LinearLayoutManager(this@PendingSettleGroupsFragment.requireContext())
        rvAdapter = PendingSettleGroupsRecyclerAdapter(
            this@PendingSettleGroupsFragment.requireContext(),
            object : PendingSettleGroupsRecyclerAdapter.Interaction {
                override fun onItemSelected(item: SettleGroupWithMovements) {
                    val directions =
                        PendingSettleGroupsFragmentDirections.actionSettleGroupsToEditGroup(item)
                    findNavController().navigate(directions)
                }
            })
        adapter = rvAdapter
    }

    private fun fetchData() = mainViewModel.settleGroups.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> {
                binding.pendingSettleGroupsProgressBar.show()
                binding.errorRetryLayout.hide()
                binding.noItemLayout.hide()
                binding.rvPending.hide()
            }
            is Resource.Success -> {
                binding.pendingSettleGroupsProgressBar.show()
                binding.errorRetryLayout.hide()
                if (result.data.isEmpty()) {
                    binding.noItemLayout.show()
                    binding.rvPending.hide()
                } else {
                    binding.noItemLayout.hide()
                    binding.rvPending.show()
                }
                rvAdapter.submitList(result.data)
            }
            is Resource.Failure -> {
                binding.pendingSettleGroupsProgressBar.show()
                binding.errorRetryLayout.show()
                binding.noItemLayout.hide()
                binding.rvPending.hide()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}