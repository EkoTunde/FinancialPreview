package com.ekosoftware.financialpreview.ui.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.databinding.FragmentPendingBinding
import com.ekosoftware.financialpreview.presentation.NavigationViewModel
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialElevationScale

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
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        adapter = PendingPagerAdapter(requireActivity()) { cardView, id, position, settleGroupWithMovementsCount ->
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            //val movementCardDetailTransitionName = Strings.get(R.string.movement_card_detail_transition_name)
            val extras = when (position) {
                0 -> FragmentNavigatorExtras(cardView to Strings.get(R.string.movement_card_detail_transition_name, id))
                1 -> FragmentNavigatorExtras(cardView to Strings.get(R.string.budget_card_detail_transition_name, id))
                else -> FragmentNavigatorExtras(
                    cardView to Strings.get(
                        R.string.settle_group_card_detail_transition_name,
                        settleGroupWithMovementsCount!!.id
                    )
                )
            }
            val directions =
                when (position) {
                    0 -> PendingFragmentDirections.actionPendingPageFragmentToDetailsMovementFragment(id)
                    1 -> PendingFragmentDirections.actionPendingPageFragmentToDetailsBudgetFragment(id)
                    else -> PendingFragmentDirections.actionPendingPageFragmentToDetailsSettleGroupFragment(settleGroupWithMovementsCount!!.id)
                }
            findNavController().navigate(directions, extras)
        }

        binding.pager.adapter = adapter
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navViewModel.setOptionForCompose(position)
            }
        })
        setTabLayoutMediator()
    }

    private val navViewModel by activityViewModels<NavigationViewModel>()

    private fun setTabLayoutMediator() = TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
        when (position + 1) {
            1 -> tab.text = getString(R.string.movements)
            2 -> tab.text = getString(R.string.budgets)
            3 -> tab.text = getString(R.string.groups)
        }
    }.attach()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}