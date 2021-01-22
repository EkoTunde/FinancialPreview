package com.ekosoftware.financialpreview.presentation.ui.pending.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.summary.MovementSummary
import com.ekosoftware.financialpreview.databinding.FragmentPendingMovementsBinding
import com.ekosoftware.financialpreview.presentation.ui.MainViewModel
import com.ekosoftware.financialpreview.presentation.ui.ShareViewModel
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters.PendingMovementsRecyclerAdapter
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show

class PendingMovementsFragment : Fragment() {

    private var _binding: FragmentPendingMovementsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val shareViewModel by activityViewModels<ShareViewModel>()

    private lateinit var rvAdapter: PendingMovementsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingMovementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        setUI()
    }

    private fun setUI() = binding.rvPending.apply {
        layoutManager = LinearLayoutManager(this@PendingMovementsFragment.requireContext())
        rvAdapter = PendingMovementsRecyclerAdapter(
            this@PendingMovementsFragment.requireContext(),
            object : PendingMovementsRecyclerAdapter.Interaction {
                override fun onItemSelected(item: MovementSummary) {
                    navigateEditFragment(item)
                }
            })
        adapter = rvAdapter
    }

    private fun fetchData() = mainViewModel.movements.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> {
                binding.pendingMovementsProgressBar.show()
                binding.errorRetryLayout.hide()
                binding.noItemLayout.hide()
                binding.rvPending.hide()
            }
            is Resource.Success -> {
                binding.pendingMovementsProgressBar.show()
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
                binding.pendingMovementsProgressBar.show()
                binding.errorRetryLayout.show()
                binding.noItemLayout.hide()
                binding.rvPending.hide()
            }
        }
    }

    private fun navigateEditFragment(item: MovementSummary) {
        shareViewModel.selectMovementId(item.movementId)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}