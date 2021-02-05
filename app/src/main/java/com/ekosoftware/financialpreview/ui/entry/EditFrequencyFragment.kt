package com.ekosoftware.financialpreview.ui.entry

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.databinding.EditFragmentFrequencyBinding
import com.ekosoftware.financialpreview.presentation.FrequencyViewModel
import com.ekosoftware.financialpreview.util.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import java.util.*

class EditFrequencyFragment : Fragment() {

    private var _binding: EditFragmentFrequencyBinding? = null
    private val binding get() = _binding!!

    private val args: EditFrequencyFragmentArgs by navArgs()

    private val frequencyViewModel: FrequencyViewModel by activityViewModels()

    private lateinit var chips: List<Chip>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditFragmentFrequencyBinding.inflate(inflater, container, false)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationOnClickListener { if (dataIsOk()) save() }
        binding.toolbar.title = Strings.get(R.string.frequency)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frequencyViewModel.getFrequency().observe(viewLifecycleOwner) {
            it?.let { frequency ->
                frequency.from?.let { from ->
                    binding.btnFrom.text = from.parseForDisplay()
                    binding.btnWhen.text = from.parseForDisplay()
                }
                frequency.to?.let { to -> binding.btnTo.text = to.parseForDisplay() }
                frequency.installments?.let { installments -> binding.numberOfQuotas.editText?.setText(installments.toString()) }
                frequency.monthsChecked.setMonths()
                if (frequency.installments != null && frequency.installments!! > 1) {
                    binding.chipInstallments.isChecked = true
                } else if (frequency.from == frequency.to) {
                    binding.chipNotRepeat.isChecked = true
                } else binding.chipRepeat.isChecked = true
                toggleMode()
            }
        }
        /*args.frequency?.let { frequency ->
            frequency.from?.let {
                binding.btnFrom.setText(it)
                binding.btnWhen.setText(it)
            }
            frequency.to?.let { binding.btnTo.setText(it) }
            frequency.installments?.let { binding.numberOfQuotas.editText?.setText(it) }
            if (frequency.installments != null && frequency.installments!! > 1) {
                binding.chipInstallments.isChecked = true
            } else if (frequency.from == frequency.to) {
                binding.chipNotRepeat.isChecked = true
            } else binding.chipRepeat.isChecked = true
            toggleMode()
        }*/

        arrayOf(binding.chipRepeat, binding.chipNotRepeat, binding.chipInstallments).forEach { it.setOnClickListener { toggleMode() } }

        with(binding) {
            listOf(btnWhen, btnFrom, btnTo).forEach { materialButton ->
                materialButton.setOnClickListener { showSelectDateDialog(materialButton) }
            }
        }

        runMonthsSelectionsSwitchMechanic()
    }

    private fun toggleMode() = with(binding) {
        when {
            chipNotRepeat.isChecked -> toggleVisibility(whenL = true)
            chipRepeat.isChecked -> toggleVisibility(from = true, to = true, months = true)
            chipInstallments.isChecked -> toggleVisibility(from = true, installments = true)
        }
    }

    private fun toggleVisibility(
        whenL: Boolean = false,
        from: Boolean = false,
        to: Boolean = false,
        installments: Boolean = false,
        months: Boolean = false
    ) = with(binding) {
        if (whenL) layoutWhen.show() else layoutWhen.hide()
        if (from) layoutFrom.show() else layoutFrom.hide()
        if (to) layoutTo.show() else layoutTo.hide()
        if (installments) {
            layoutInstallmentsAmount.show()
            layoutInstallmentsResult.show()
        } else {
            layoutInstallmentsAmount.hide()
            layoutInstallmentsResult.hide()
        }
        if (months) layoutMonths.show() else layoutMonths.hide()
    }

    // Handles months' selection mechanics for their selection
    private fun runMonthsSelectionsSwitchMechanic() = binding.apply {
        // Creates the list of chips
        chips = listOf(chipJan, chipFeb, chipMar, chipApr, chipMay, chipJun, chipJul, chipAug, chipSep, chipOct, chipNov, chipDec)
        chips.forEach { chip -> // For every chip clicked
            chip.setOnCheckedChangeListener { _, isChecked ->
                // if it's not checked set "select all / un-select all" button to "Select all"
                if (!isChecked) {
                    val content = SpannableString(resources.getString(R.string.select_all))
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    btnSelectUnselectAllMonths.text = content
                }
                // Run checking chips selection
                changeBtnTextWhenAllSelected()
            }
        }

        // When switching selection button is clicked
        btnSelectUnselectAllMonths.setOnClickListener {
            // Check if it's text is "select all", check all chips if true, else set unchecked them all
            if ((it as MaterialButton).text.toString() == resources.getString(R.string.select_all)) chips.forEach { chip -> chip.isChecked = true }
            else chips.forEach { chip -> chip.isChecked = false }
        }
    }

    private fun changeBtnTextWhenAllSelected() = binding.apply {
        // Maps chips' list to a boolean list after checking whether it's checked,
        // Then checks if it contains a non-checked and outputs the opposite
        val allSelected = !chips.map { it.isChecked }.contains(false)
        // If they are all selected change the text of the switching button
        if (allSelected) {
            //btnSelectUnselectAllMonths.text = resources.getString(R.string.unselect_all)
            val content = SpannableString(resources.getString(R.string.unselect_all))
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            btnSelectUnselectAllMonths.text = content
        }
    }

    private fun showSelectDateDialog(btn: MaterialButton) {

        // Inflate dialog's view
        val view = layoutInflater.inflate(R.layout.alert_dialog_date_selector, binding.root, false)

        val monthPicker = view.findViewById<NumberPicker>(R.id.month_picker)
        val yearPicker = view.findViewById<NumberPicker>(R.id.year_picker)

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Get current month and set it to month var, used when dialog is dismissed with positive button
        var month = btn.currentTextToMonth() ?: currentMonth
        // Get current year and set it to month var, used when dialog is dismissed with positive button
        var year = btn.currentTextToYear() ?: currentYear


        // Set month's number picker values
        monthPicker.apply {
            maxValue = 12
            minValue = 1
            value = month
            setOnValueChangedListener { _, oldVal, newVal ->
                if (oldVal == 12 && newVal == 1 && yearPicker.value != yearPicker.maxValue) yearPicker.value += 1
                if (oldVal == 1 && newVal == 12 && yearPicker.value != yearPicker.minValue) yearPicker.value -= 1
                year = yearPicker.value
                month = newVal
            }
        }

        yearPicker.apply {
            maxValue = year + 50
            minValue = year
            value = year
            setOnValueChangedListener { _, _, newVal -> year = newVal }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.select_month_and_year))
            .setView(view)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                val result = "$year/${month.toString().padStart(2, '0')}"
                btn.text = result
                errorFromToBtns(btn)
            }
        if (btn == binding.btnTo) dialog.setNeutralButton(R.string.clean) { _, _ ->
            btn.text = Strings.get(R.string.to)
        }
        dialog.show()
    }

    private fun errorFromToBtns(btn: MaterialButton) = with(binding) {
        if (btnFrom.currentTextToInt() != null && btnTo.currentTextToInt() != null) {
            if (btn == btnFrom && btnFrom.currentTextToInt()!! > btnTo.currentTextToInt()!!) {
                Snackbar.make(binding.mainContainer, R.string.selected_to_cant_be_bigger_than_from, Snackbar.LENGTH_LONG)
                    .setAction(Strings.get(R.string.fix)) {
                        showSelectDateDialog(btnFrom)
                    }.show()
            }
            if (btn == btnTo && btnTo.currentTextToInt()!! < btnFrom.currentTextToInt()!!) {
                Snackbar.make(binding.mainContainer, R.string.selected_from_cant_be_bigger_than_to, Snackbar.LENGTH_LONG)
                    .setAction(Strings.get(R.string.fix)) {
                        showSelectDateDialog(btnTo)
                    }.show()
            }
        }
    }

    private fun dataIsOk(): Boolean {
        when {
            binding.chipNotRepeat.isChecked -> {
                val _when = binding.btnWhen.text.toString()
                if (_when.isNotEmpty() && _when != Strings.get(R.string.`when`) && "/" in _when) return true
                Snackbar.make(binding.mainContainer, R.string.must_indicate_when, Snackbar.LENGTH_LONG).setAction(R.string.fix) {
                    showSelectDateDialog(binding.btnWhen)
                }.show()
                return false
            }
            binding.chipRepeat.isChecked -> {
                val from = binding.btnFrom.text.toString()
                if (from.isNotEmpty() && from != Strings.get(R.string.from) || "/" in from) return true
                Snackbar.make(binding.mainContainer, getString(R.string.start_is_mandatory), Snackbar.LENGTH_LONG).setAction(R.string.fix) {
                    showSelectDateDialog(binding.btnFrom)
                }.show()
                return false
            }
            binding.chipInstallments.isChecked -> {
                val from = binding.btnFrom.text.toString()
                if (from.isEmpty() || from == Strings.get(R.string.`when`) || "/" !in from) {
                    Snackbar.make(binding.mainContainer, getString(R.string.start_is_mandatory), Snackbar.LENGTH_LONG).setAction(R.string.fix) {
                        showSelectDateDialog(binding.btnFrom)
                    }.show()
                    return false
                }
                val quotas = binding.numberOfQuotas.editText?.text.toString()
                if (quotas.isEmpty() || quotas.toInt() < 2) {
                    Snackbar.make(binding.mainContainer, getString(R.string.cant_set_less_than_two_quotas), Snackbar.LENGTH_LONG).show()
                    return false
                }
                return true
            }
            else -> return false
        }
    }

    private fun save() = with(binding) {
        frequencyViewModel.setInput(
            type = when {
                chipNotRepeat.isChecked -> FrequencyViewModel.FrequencyType.NON_REPEAT_FREQUENCY
                chipRepeat.isChecked -> FrequencyViewModel.FrequencyType.REPEAT_FREQUENCY
                chipInstallments.isChecked -> FrequencyViewModel.FrequencyType.INSTALLMENTS_BASED_FREQUENCY
                else -> throw IllegalStateException("A chip mut be selected to determine a frequency type")
            },
            _when = btnWhen.text.toString(),
            from = btnFrom.text.toString(),
            to = btnTo.text.toString(),
            _installments = numberOfQuotas.editText?.text.toString(),
            months = monthsToString()
        )
        findNavController().navigateUp()
    }

    private fun Int.parseForDisplay(): String {
        if (this.toString().length != 6) throw IllegalArgumentException("The Int variable \"this\" must be 6 digits long, as in 202105")
        if (this == 999999) return Strings.get(R.string.to)
        return "${this.getYear()}/${this.getMonthAsString()}"
    }

    private fun MaterialButton.currentTextToInt(): Int? {
        return if ("/" in this.text.toString()) this.text.toString().replace("/", "").toInt() else null
    }

    private fun MaterialButton.currentTextToMonth(): Int? {
        return if ("/" in this.text.toString()) this.text.toString().replace("/", "").toInt().getMonth() else null
    }

    private fun MaterialButton.currentTextToYear(): Int? {
        return if ("/" in this.text.toString()) this.text.toString().replace("/", "").toInt().getYear() else null
    }

    private fun String.setMonths() {
        val months = this.split("-")
        with(binding) {
            arrayOf(
                chipJan, chipFeb, chipMar, chipApr, chipMay, chipJun,
                chipJul, chipAug, chipSep, chipOct, chipNov, chipDec
            ).forEachIndexed { index, chip -> chip.isChecked = months[index] != Constants.noMonth }
        }
    }

    private fun monthsToString(): String = with(binding) {
        val result = StringBuilder()
        arrayOf(
            chipJan, chipFeb, chipMar, chipApr, chipMay, chipJun,
            chipJul, chipAug, chipSep, chipOct, chipNov, chipDec
        ).forEachIndexed { index, chip ->
            if (index > 0) result.append("-")
            if (chip.isChecked) result.append((index + 1).parseToAbbreviatedString()) else result.append("000")
        }
        return result.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}