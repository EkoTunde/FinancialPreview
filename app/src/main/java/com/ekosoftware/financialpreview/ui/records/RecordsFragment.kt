package com.ekosoftware.financialpreview.ui.records

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.databinding.FragmentRecordsBinding
import com.ekosoftware.financialpreview.presentation.AccountsAndRecordsViewModel
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show

class RecordsFragment : Fragment() {
    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!

    private val accountsAndRecordsViewModel by activityViewModels<AccountsAndRecordsViewModel>()

    private val recordsAdapter = RecordsListAdapter {
        val directions = RecordsFragmentDirections.recordsToEditRecord(it.id)
        findNavController().navigate(directions)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
            adapter = recordsAdapter
        }
        fetchData()
    }

    private fun fetchData() =
        accountsAndRecordsViewModel.records.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.show()
                    binding.rvRecords.hide()
                    binding.noItemLayout.hide()
                    binding.errorRetryLayout.hide()
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isNullOrEmpty()) {
                        binding.noItemLayout.show()
                    } else {
                        recordsAdapter.submitList(result.data)
                        binding.rvRecords.show()
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.hide()
                    binding.errorRetryLayout.show()
                }
            }
        })

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}