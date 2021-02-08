package com.ekosoftware.financialpreview.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Constants.SETTLE_TYPE_LOAN_DEBT
import com.ekosoftware.financialpreview.app.Constants.SETTLE_TYPE_SIMPLE_RECORD
import com.ekosoftware.financialpreview.app.Constants.SETTLE_TYPE_TRANSFER
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.HomeData
import com.ekosoftware.financialpreview.databinding.FragmentHomeBinding
import com.ekosoftware.financialpreview.presentation.HomeViewModel
import com.ekosoftware.financialpreview.util.*
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_home_05_quick_view.view.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<HomeViewModel>()

    private lateinit var tf: Typeface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(
            R.menu.home_menu
        )
        binding.toolbar.setOnMenuItemClickListener {
            mainViewModel.refresh()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.refresh()
        fetchData()
        setUpActions()
        binding.balance.currentTotal.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomePageFragmentToAccounts()
            findNavController().navigate(directions)
        }
    }

    private fun fetchData() {
        mainViewModel.getHomeData().observe(viewLifecycleOwner, { result ->
            with(binding) {
                when (result) {
                    is Resource.Loading -> {
                        progressBar.show()
                        scrollViewContainer.hide()
                    }
                    is Resource.Success -> {
                        progressBar.hide()
                        scrollViewContainer.show()
                        setUpBalance(result.data.currencyCode, result.data.currentBalance())
                        setUpPendingSummary(result.data)
                        setUpProjection(result.data)
                        setUpQuickView(result.data)
                    }
                    is Resource.Failure -> {
                        progressBar.hide()
                        scrollViewContainer.show()
                    }
                }
            }
        })
    }

    private fun setUpBalance(currencyCode: String, balance: Double) = with(binding.balance) {
        currentTotal.applyMoneyFormat(currencyCode, balance)
    }

    private fun setUpActions() {
        hashMapOf(
            binding.actions.btnSettle to HomeFragmentDirections.actionHomePageFragmentToSettleOptionsDialog(),
            binding.actions.btnAddPending to HomeFragmentDirections.actionGlobalEditMovementFragment(Constants.nan),
            binding.actions.btnAddRecord to HomeFragmentDirections.actionGlobalSettleFragment(SETTLE_TYPE_SIMPLE_RECORD, null),
            binding.actions.btnTransfer to HomeFragmentDirections.actionGlobalSettleFragment(SETTLE_TYPE_TRANSFER, null),
            binding.actions.btnLoan to HomeFragmentDirections.actionGlobalSettleFragment(SETTLE_TYPE_LOAN_DEBT, null),
        ).forEach { (btn, action) ->
            btn.setOnClickListener { findNavController().navigate(action) }
        }
    }

    private fun setUpPendingSummary(homeData: HomeData) {
        with(binding.pending) {
            income.applyMoneyFormat(homeData.currencyCode, homeData.incomes()[0])
            income.applyShader(R.color.colorAmountPositiveGradient1, R.color.colorAmountPositiveGradient2, R.color.colorAmountPositiveGradient3)
            expenses.applyMoneyFormat(homeData.currencyCode, homeData.expenses()[0])
            expenses.applyShader(R.color.colorAmountNegativeGradient1, R.color.colorAmountNegativeGradient2, R.color.colorAmountNegativeGradient3)
        }
    }

    private fun setUpProjection(homeData: HomeData) = with(binding.projection) {
        thisMonthSavingAmount.applyMoneyFormatInK(
            homeData.currencyCode,
            homeData.accumulatedBalances()[1]
        )
        sixMonthSavingAmount.applyMoneyFormatInK(
            homeData.currencyCode,
            homeData.accumulatedBalances()[7]
        )
        thisYearSavingAmount.applyMoneyFormatInK(
            homeData.currencyCode,
            homeData.accumulatedBalances()[13]
        )
    }

    private fun setUpQuickView(homeData: HomeData) = binding.quickView.apply {
        tf = ResourcesCompat.getFont(requireContext(), R.font.quicksand_medium)!!
        quickViewChart.setUpChart(homeData)
        quickViewChart.setListener(homeData)
    }

    private fun CombinedChart.setUpChart(homeData: HomeData) = this.apply {
        // Set data
        val barData = getBarData(
            homeData.incomes(),
            homeData.expenses(),
            homeData.monthsNames()
        )
        val lineData =
            getLineData(homeData.balances(), homeData.accumulatedBalances(), homeData.monthsNames())
        val combinedData = CombinedData()
        combinedData.setData(barData) // set BarData
        combinedData.setData(lineData) // set LineData
        data = combinedData
        invalidate()

        // Apply UI settings
        this@setUpChart.description = null
        applyXAxisSettings(resources.getStringArray(R.array.months_abr).toList())
        applyLegendSettings()

        // Apply typeface
        arrayOf(axisLeft, axisRight).forEach { it.typeface = tf }

        isDragEnabled = true
        animateY(2000)
    }

    private fun CombinedChart.setListener(homeData: HomeData) {
        setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val amount = when (h?.stackIndex) {
                    0 -> (e as BarEntry).yVals[0]
                    1 -> (e as BarEntry).yVals[1]
                    else -> h?.y
                }
                val type = when (h?.stackIndex) {
                    0 -> context.getString(R.string.from_income)
                    1 -> context.getString(R.string.from_expense)
                    else -> when (h?.dataSetIndex) {
                        0 -> context.getString(R.string.from_accumulated)
                        else -> context.getString(R.string.from_balance)
                    }
                }
                val output =
                    Strings.get(
                        R.string.chart_description_output,
                        homeData.currencyCode,
                        amount ?: .0f,
                        type,
                        e?.data ?: ""
                    )
                binding.quickView.detail.text = output
            }

            override fun onNothingSelected() = Unit
        })
    }

    private fun getBarData(
        incomes: List<Double>,
        expenses: List<Double>,
        months: List<String>
    ): BarData {
        val entries: MutableList<BarEntry> = mutableListOf()
        incomes.indices.forEach { i ->
            val ys = floatArrayOf(incomes[i].toFloat(), expenses[i].toFloat())
            entries.add(BarEntry(i.toFloat(), ys, months[i]))
        }

        val stackedSet = BarDataSet(entries, "")
        stackedSet.setDrawIcons(false)

        val incomeColor = ColorTemplate.MATERIAL_COLORS[0]    // Material green
        val expenseColor = ColorTemplate.MATERIAL_COLORS[2]      // Material red

        stackedSet.setColors(incomeColor, expenseColor)
        stackedSet.stackLabels =
            arrayOf(stringResource(R.string.incomes), stringResource(R.string.expenses))

        val dataSets: MutableList<IBarDataSet> = ArrayList()
        dataSets.add(stackedSet)

        return BarData(dataSets).apply {
            setValueFormatter(LargeValueFormatter())
            setDrawValues(false)
            setValueTypeface(tf)
        }
    }

    private fun getLineData(
        balance: List<Double>,
        accumulated: List<Double>,
        months: List<String>
    ): LineData {

        val balanceEntries: MutableList<Entry> = mutableListOf()
        val diffEntries: MutableList<Entry> = mutableListOf()

        balance.indices.forEach { i ->
            val balanceEntry = BarEntry(i.toFloat(), balance[i].toFloat(), months[i])
            balanceEntries.add(balanceEntry)
            val diffEntry = BarEntry(i.toFloat(), accumulated[i].toFloat(), months[i])
            diffEntries.add(diffEntry)
        }

        val lineDataSetBalance =
            LineDataSet(balanceEntries, Strings.get(R.string.balances)).apply {
                this.axisDependency = YAxis.AxisDependency.LEFT
                val colorBalance = ColorTemplate.MATERIAL_COLORS[1]   // Material yellow
                this.setColors(colorBalance)
                this.color = colorBalance
                this.setCircleColor(colorBalance)
                this.lineWidth = 2.5f
                this.circleRadius = 4f
            }

        val lineDataSetDiffs =
            LineDataSet(diffEntries, Strings.get(R.string.balance_acummulated)).apply {
                this.axisDependency = YAxis.AxisDependency.LEFT
                val colorDiffs = ColorTemplate.MATERIAL_COLORS[3]
                this.setColors(ColorTemplate.MATERIAL_COLORS[3])    // Material blue
                this.color = colorDiffs
                this.setCircleColor(colorDiffs)
                this.lineWidth = 2.5f
                this.circleRadius = 4f
            }

        val dataSets: MutableList<ILineDataSet> = ArrayList()
        dataSets.add(lineDataSetBalance)
        dataSets.add(lineDataSetDiffs)

        return LineData(dataSets).apply {
            setValueFormatter(LargeValueFormatter())
            setDrawValues(false)
            setValueTypeface(tf)
        }
    }

    private fun CombinedChart.applyXAxisSettings(axisNames: List<String>) = this.xAxis.apply {
        isEnabled = true
        position = XAxis.XAxisPosition.BOTTOM_INSIDE
        setDrawLabels(true)
        granularity = 1f
        labelRotationAngle = +0f
        valueFormatter = IndexAxisValueFormatter(axisNames)
        spaceMin = barData.barWidth / 2f // Make Graphs fit
        spaceMax = barData.barWidth / 2f // Make Graphs fit
        setDrawGridLines(false)
        typeface = tf
    }

    private fun CombinedChart.applyLegendSettings() = this.legend.apply {
        isEnabled = true
        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        orientation = Legend.LegendOrientation.HORIZONTAL
        setDrawInside(false)
        form = Legend.LegendForm.SQUARE
        typeface = tf
        formSize = 9f
        textSize = 11f
        xEntrySpace = 4f
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}