package com.ekosoftware.financialpreview.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * Base parent class to be extended by simple [Fragment]s
 * that need to display a list of data, with loading, success
 * with data, success without data and failure UI responses.
 * Provides fully automatic UI responses to [LiveData] objects
 * that contain [Resource]s wrapping [T] data.
 * It uses [ViewBinding] features with [K] provided to bind [BaseListAdapter]
 * sublclass items to views.
 * Also supports failure and "Retry Policy".
 *
 */
abstract class BaseListFragment<T, K : ViewBinding> : Fragment() {

    private var _binding: BaseListFragmentBinding? = null
    private val binding get() = _binding!!

    /**
     * Instance of a custom [androidx.recyclerview.widget.ListAdapter]
     * class which holds and displays [T] data with [K] xml layouts.
     */
    protected abstract val listAdapter: BaseListAdapter<T, K>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BaseListFragmentBinding.inflate(inflater, container, false)
        onCreateToolbar(binding.appBarLayout, binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRetryButton()
        setUpRecyclerView()
    }


    /**
     * Gives subclasses a chance to use [BaseListFragment]'s [Toolbar] and/or [AppBarLayout].
     */
    open fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        return appBarLayout.hide()
    }

    /**
     * Sets [MaterialButton] "retryButton" to trigger [onRetry].
     */
    private fun setUpRetryButton() = binding.retryButton.setOnClickListener {
        onRetry(binding)
    }

    /**
     * Initialize [RecyclerView]'s properties. By default, it's
     * set with a [LinearLayoutManager] as [RecyclerView.LayoutManager].
     * Calls for [recyclerViewDividerOrientation] to set [DividerItemDecoration].
     * Finally, sets [RecyclerView.Adapter] to [listAdapter].
     */
    private fun setUpRecyclerView() = with(binding.recyclerView) {
        layoutManager = LinearLayoutManager(requireContext())
        recyclerViewDividerOrientation()?.let { divider ->
            this.addItemDecoration(DividerItemDecoration(requireContext(), divider))
        }
        adapter = listAdapter
    }

    /**
     * Called by [setUpRecyclerView]. Gives subclasses a chance to
     * set a divider orientation (e.g.: LinearLayout.VERTICAL). Returns null
     * by default, which leads to no [DividerItemDecoration] for [RecyclerView] at all.
     *
     */
    open fun recyclerViewDividerOrientation(): Int? = null

    /**
     * Called to trigger loading UI responses
     */
    fun loading() = toggleVisibility(true)

    /**
     * Called to trigger success UI responses when it contains data.
     */
    fun success() = toggleVisibility(recyclerViewVisible = true, fabVisible = true)

    /**
     * Called to trigger success UI responses when on data was returned.
     */
    fun successNaN() = toggleVisibility(noItemLayoutVisible = true, fabVisible = true)

    /**
     * Called to trigger failure UI responses.
     */
    fun failure() = toggleVisibility(errorRetryLayoutVisible = true)


    /**
     * Toggles [K]'s [View]s responsible for UI states visibility
     * when called from different triggers.
     * @param progressBarVisible wether  [ProgressBar] that indicates data is loading is visible.
     * @param recyclerViewVisible wether  [RecyclerView] for displaying data is visible when it is successfully retrieved.
     * @param noItemLayoutVisible wether  [LinearLayout] that warns no data was retrieved and it can be added is visible.
     * @param errorRetryLayoutVisible wether  [LinearLayout] for error warning and "retry" [android.widget.Button] are displayed.
     * @param fabVisible wether  [FloatingActionButton] for adding new data entries is visible.
     */
    private fun toggleVisibility(
        progressBarVisible: Boolean = false,
        recyclerViewVisible: Boolean = false,
        noItemLayoutVisible: Boolean = false,
        errorRetryLayoutVisible: Boolean = false,
        fabVisible: Boolean = false,
    ) {
        if (progressBarVisible) binding.progressBar.show() else binding.progressBar.hide()
        if (recyclerViewVisible) binding.recyclerView.show() else binding.recyclerView.hide()
        if (noItemLayoutVisible) binding.noItemLayout.show() else binding.noItemLayout.hide()
        if (errorRetryLayoutVisible) binding.errorRetryLayout.show() else binding.errorRetryLayout.hide()
        if (fabVisible) binding.fabAdd.show() else binding.fabAdd.hide()
    }

    /**
     * Attaches an observer to a [LiveData] objects which holds a [Resource]
     * wrapping a [List] of [T]. Handles [Resource] instances for loading,
     * success and failure states, and triggers UI responses to those events.
     * When data is received, [onReceived] lambda is triggered from subclasses,
     * and the received data is sent to them.
     */
    fun LiveData<Resource<List<T>>>.fetchData(onReceived: (List<T>) -> Unit) {
        if (hasObservers()) removeObservers(viewLifecycleOwner)
        observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    loading()
                }
                is Resource.Success -> {
                    if (result.data.isNullOrEmpty()) successNaN() else {
                        success()
                        onReceived(result.data)
                    }
                }
                is Resource.Failure -> {
                    failure()
                }
            }
        }
    }

    fun hideFab() {
        binding.fabAdd.hide()
    }

    /**
     * Called from "Retry" [MaterialButton] for "Retry Policy".
     */
    open fun onRetry(
        binding: BaseListFragmentBinding
    ) = Unit
}