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
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.FragmentPendingBudgetsBinding
import com.ekosoftware.financialpreview.presentation.ui.MainViewModel
import com.ekosoftware.financialpreview.presentation.ui.ShareViewModel
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters.PendingBudgetsRecyclerAdapter
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show

class PendingBudgetsFragment : Fragment() {

    private var _binding: FragmentPendingBudgetsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val shareViewModel by activityViewModels<ShareViewModel>()

    private lateinit var rvAdapter: PendingBudgetsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        fetchData()
    }

    private fun setUI() = binding.rvPendingBudgets.apply {
        layoutManager = LinearLayoutManager(this@PendingBudgetsFragment.requireContext())
        rvAdapter = PendingBudgetsRecyclerAdapter(
            this@PendingBudgetsFragment.requireContext(),
            object : PendingBudgetsRecyclerAdapter.Interaction {
                override fun onItemSelected(item: Budget) {
                    val directions =
                        PendingBudgetsFragmentDirections.actionPendingBudgetsToEditBudget(item.id)
                    findNavController().navigate(directions)
                }
            })
        adapter = rvAdapter
    }

    private fun fetchData() = mainViewModel.budgets.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> {
                binding.pendingBudgetsProgressBar.show()
                binding.errorRetryLayout.hide()
                binding.noItemLayout.hide()
                binding.rvPendingBudgets.hide()
            }
            is Resource.Success -> {
                binding.pendingBudgetsProgressBar.show()
                binding.errorRetryLayout.hide()
                if (result.data.isEmpty()) {
                    binding.noItemLayout.show()
                    binding.rvPendingBudgets.hide()
                } else {
                    binding.noItemLayout.hide()
                    binding.rvPendingBudgets.show()
                }
                rvAdapter.submitList(result.data)
            }
            is Resource.Failure -> {
                binding.pendingBudgetsProgressBar.show()
                binding.errorRetryLayout.show()
                binding.noItemLayout.hide()
                binding.rvPendingBudgets.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}