package com.ekosoftware.financialpreview.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.account.Balance
import com.ekosoftware.financialpreview.data.model.movement.QuickViewSummary
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.databinding.FragmentHomeBinding
import com.ekosoftware.financialpreview.presentation.MainViewModel
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
import kotlinx.android.synthetic.main.item_home_05_quick_view.view.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var tf: Typeface

    companion object {
        private const val TAG = "NowFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadBalanceData()
        setUpActions()
        loadPendingSummaryData()
        //loadProjectionData()
        loadQuickViewData()

        binding.balance.currentTotal.setOnClickListener {
            val directions = HomeFragmentDirections.homeToAccounts()
            findNavController().navigate(directions)
        }
    }

    private fun loadBalanceData() = mainViewModel.balance.observe(viewLifecycleOwner, { result ->
        when (result) {
            is Resource.Loading -> {
                binding.progressBar.show()
                binding.scrollViewContainer.hide()
            }
            is Resource.Success -> {
                Log.d(TAG, "loadBalanceData: result is ${result.data}")
                setUpBalance(result.data)
                binding.progressBar.hide()
                binding.scrollViewContainer.show()
            }
            is Resource.Failure -> {
                binding.progressBar.hide()
                binding.scrollViewContainer.show()
            }
        }
    })

    private fun setUpBalance(data: Balance) = binding.balance.apply {
        currentTotal.applyMoneyFormat(data.currency, data.total)

        // Cambiar moneda!!
        /*btnChangeCurrency.setOnClickListener {

        }*/
    }

    private fun selectCurrencyDialog() {

    }

    private fun settle() {

    }

    private fun addPreview() {

    }

    private fun addRecord() {

    }

    private fun transfer() {

    }

    private fun setUpActions() = binding.actions.apply {
        btnSettle.setOnClickListener { /*onSettleSelected()*/ }
        btnAddPending.setOnClickListener { /*onAddPendingSelected()*/ }
        btnAddRecord.setOnClickListener { /*onAddRecordSelected()*/ }
        btnTransfer.setOnClickListener { /*onTransferSelected()*/ }
    }

    private fun loadPendingSummaryData() =
        mainViewModel.monthSummary.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.pending.item03ProgressBar.show()
                    binding.pending.layoutPendingSummary.hide()
                    binding.pending.buttonRetry.hide()
                }
                is Resource.Success -> {
                    setUpPendingSummary(result.data)
                    binding.pending.item03ProgressBar.hide()
                    binding.pending.layoutPendingSummary.show()
                    binding.pending.buttonRetry.hide()
                }
                is Resource.Failure -> {
                    binding.pending.item03ProgressBar.hide()
                    binding.pending.layoutPendingSummary.hide()
                    binding.pending.buttonRetry.show()
                }
                else -> throw IllegalStateException("loadPendingSummaryData should not output a loading resource.")
            }
        })

    private fun setUpPendingSummary(summary: MonthSummary) = binding.pending.apply {
        income.applyMoneyFormat(summary.currencyCode, summary.totalIncome ?: 0.0)
        income.applyShader(
            R.color.colorAmountPositiveGradient1,
            R.color.colorAmountPositiveGradient2,
            R.color.colorAmountPositiveGradient3
        )
        expenses.applyMoneyFormat(summary.currencyCode, summary.totalExpense ?: 0.0)
        expenses.applyShader(
            R.color.colorAmountNegativeGradient1,
            R.color.colorAmountNegativeGradient2,
            R.color.colorAmountNegativeGradient3
        )
    }

    /*private fun loadProjectionData() =
        homeViewModel.quickViewSummary.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.projection.item04ProgressBar.show()
                    binding.projection.topLayout.hide()
                    binding.projection.timelineLayout.hide()
                    binding.projection.buttonRetry.hide()
                }
                is Resource.Success -> {
                    setUpProjection(result.data)
                    binding.projection.item04ProgressBar.hide()
                    binding.projection.topLayout.show()
                    binding.projection.timelineLayout.show()
                    binding.projection.buttonRetry.hide()
                }
                is Resource.Failure -> {
                    binding.projection.item04ProgressBar.hide()
                    binding.projection.topLayout.hide()
                    binding.projection.timelineLayout.hide()
                    binding.projection.buttonRetry.show()
                }
                else -> throw IllegalStateException("loadProjectionData should not output a loading resource.")
            }
        })*/

    private fun setUpProjection(quickViewSummary: QuickViewSummary) = binding.projection.apply {
        thisMonthSavingAmount.applyMoneyFormat(
            quickViewSummary.currencyCode,
            quickViewSummary.accumulatedBalances[0]
        )
        sixMonthSavingAmount.applyMoneyFormat(
            quickViewSummary.currencyCode,
            quickViewSummary.accumulatedBalances[6]
        )
        thisYearSavingAmount.applyMoneyFormat(
            quickViewSummary.currencyCode,
            quickViewSummary.accumulatedBalances[12]
        )
    }

    private fun loadQuickViewData() =
        mainViewModel.quickViewSummary.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.projection.item04ProgressBar.show()
                    binding.projection.topLayout.hide()
                    binding.projection.timelineLayout.hide()
                    binding.projection.buttonRetry.hide()
                    binding.quickView.item05ProgressBar.show()
                    binding.quickView.quickViewChart.hide()
                    binding.quickView.detail.hide()
                    binding.quickView.buttonRetry.hide()
                }
                is Resource.Success -> {
                    setUpQuickView(result.data)
                    binding.quickView.item05ProgressBar.hide()
                    binding.quickView.quickViewChart.show()
                    binding.quickView.detail.show()
                    binding.quickView.buttonRetry.hide()
                    setUpProjection(result.data)
                    binding.projection.item04ProgressBar.hide()
                    binding.projection.topLayout.show()
                    binding.projection.timelineLayout.show()
                    binding.projection.buttonRetry.hide()
                }
                is Resource.Failure -> {
                    binding.quickView.item05ProgressBar.hide()
                    binding.quickView.quickViewChart.hide()
                    binding.quickView.detail.hide()
                    binding.quickView.buttonRetry.show()
                    binding.projection.item04ProgressBar.hide()
                    binding.projection.topLayout.hide()
                    binding.projection.timelineLayout.hide()
                    binding.projection.buttonRetry.show()
                    Log.d(TAG, "loadQuickViewData: ERROR = ${result.exception}")
                }
                else -> throw IllegalStateException("loadOneYearSummaryData should not output a loading resource.")
            }
        })

    private fun setUpQuickView(quickViewSummary: QuickViewSummary) = binding.quickView.apply {
        tf = ResourcesCompat.getFont(requireContext(), R.font.quicksand_medium)!!
        quickViewChart.setUpChart(quickViewSummary)
    }

    private fun CombinedChart.setUpChart(quickViewSummary: QuickViewSummary) = this.apply {
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
                    context.getString(
                        R.string.chart_description_output,
                        quickViewSummary.currencyCode,
                        amount,
                        type,
                        e?.data
                    )
                binding.quickView.detail.text = output
            }

            override fun onNothingSelected() = Unit
        })

        // Set data
        val barData =
            getBarData(quickViewSummary.incomes, quickViewSummary.expenses, quickViewSummary.months)
        val lineData =
            getLineData(
                quickViewSummary.balances,
                quickViewSummary.accumulatedBalances,
                quickViewSummary.months
            )
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
        differences: List<Double>,
        months: List<String>
    ): LineData {

        val balanceEntries: MutableList<Entry> = mutableListOf()
        val diffEntries: MutableList<Entry> = mutableListOf()

        balance.indices.forEach { i ->
            val balanceEntry = BarEntry(i.toFloat(), balance[i].toFloat(), months[i])
            balanceEntries.add(balanceEntry)
            val diffEntry = BarEntry(i.toFloat(), differences[i].toFloat(), months[i])
            diffEntries.add(diffEntry)
        }

        val lineDataSetBalance =
            LineDataSet(balanceEntries, stringResource(R.string.balance_acummulated)).apply {
                this.axisDependency = YAxis.AxisDependency.LEFT
                val colorBalance = ColorTemplate.MATERIAL_COLORS[1]   // Material yellow
                this.setColors(colorBalance)
                this.color = colorBalance
                this.setCircleColor(colorBalance)
                this.lineWidth = 2.5f
                this.circleRadius = 4f
            }

        val lineDataSetDiffs = LineDataSet(diffEntries, "Diferencias").apply {
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

/*
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
        arrayOf(context.getString(R.string.incomes), context.getString(R.string.expenses))

    val dataSets: MutableList<IBarDataSet> = ArrayList()
    dataSets.add(stackedSet)

    */
/*val barData = BarData(dataSets)
    barData.setValueFormatter(LargeValueFormatter())
    barData.setDrawValues(false)
    barData.setValueTypeface(tf)*//*

    return BarData(dataSets).apply {
        setValueFormatter(LargeValueFormatter())
        setDrawValues(false)
        setValueTypeface(tf)
    }
}

private fun getLineData(
    balance: List<Double>,
    differences: List<Double>,
    months: List<String>
): LineData {

    val balanceEntries: MutableList<Entry> = mutableListOf()
    val diffEntries: MutableList<Entry> = mutableListOf()

    balance.indices.forEach { i ->
        val balanceEntry = BarEntry(i.toFloat(), balance[i].toFloat(), months[i])
        balanceEntries.add(balanceEntry)
        val diffEntry = BarEntry(i.toFloat(), differences[i].toFloat(), months[i])
        diffEntries.add(diffEntry)
    }

    val lineDataSetBalance =
        LineDataSet(balanceEntries, context.getString(R.string.balance_acummulated)).apply {
            this.axisDependency = YAxis.AxisDependency.LEFT
            val colorBalance = ColorTemplate.MATERIAL_COLORS[1]   // Material yellow
            this.setColors(colorBalance)
            this.color = colorBalance
            this.setCircleColor(colorBalance)
            this.lineWidth = 2.5f
            this.circleRadius = 4f
        }

    */
/*val lineDataSetBalance = LineDataSet(balanceEntries, context.getString(R.string.balance_acummulated))
    lineDataSetBalance.axisDependency = YAxis.AxisDependency.LEFT

    val colorBalance = ColorTemplate.MATERIAL_COLORS[1]   // Material yellow
    lineDataSetBalance.setColors(colorBalance)

    lineDataSetBalance.lineWidth = 2.5f
    lineDataSetBalance.circleRadius = 4f
    lineDataSetBalance.color = colorBalance
    lineDataSetBalance.setCircleColor(colorBalance)*//*


    val lineDataSetDiffs = LineDataSet(diffEntries, "Diferencias").apply {
        this.axisDependency = YAxis.AxisDependency.LEFT
        val colorDiffs = ColorTemplate.MATERIAL_COLORS[3]
        this.setColors(ColorTemplate.MATERIAL_COLORS[3])    // Material blue
        this.color = colorDiffs
        this.setCircleColor(colorDiffs)
        this.lineWidth = 2.5f
        this.circleRadius = 4f
    }
    */
/*lineDataSetDiffs.axisDependency = YAxis.AxisDependency.LEFT

    val colorDiffs = ColorTemplate.MATERIAL_COLORS[3]   // Material blue
    lineDataSetDiffs.setColors(colorDiffs)

    lineDataSetDiffs.lineWidth = 2.5f
    lineDataSetDiffs.circleRadius = 4f
    lineDataSetDiffs.color = colorDiffs
    lineDataSetDiffs.setCircleColor(colorDiffs)*//*


    val dataSets: MutableList<ILineDataSet> = ArrayList()
    dataSets.add(lineDataSetBalance)
    dataSets.add(lineDataSetDiffs)

    */
/*val lineData = LineData(dataSets)
    lineData.setValueFormatter(LargeValueFormatter())
    lineData.setDrawValues(false)
    lineData.setValueTypeface(tf)*//*

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
}*/
