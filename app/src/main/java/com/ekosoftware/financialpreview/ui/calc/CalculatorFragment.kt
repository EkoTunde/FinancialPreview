package com.ekosoftware.financialpreview.ui.calc

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.Operation
import com.ekosoftware.financialpreview.databinding.FragmentCalculatorBinding
import com.ekosoftware.financialpreview.presentation.CalculatorViewModel
import com.ekosoftware.financialpreview.util.hasUselessDecimals
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private val args: CalculatorFragmentArgs by navArgs()

    private val calculatorViewModel: CalculatorViewModel by activityViewModels()

    private var currentOperation = Operation.NONE
    private var memory = ""
    private var override = false

    private val amount by lazy { args.amount }
    private val currencyDecimalPoints by lazy { args.decimalPoints }

    private lateinit var localeComma: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        // Setup toolbar with navigation functionality and inflates de save menu
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.inflateMenu(R.menu.save_menu)
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_save -> {
                done()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localeComma = ","
        amount?.let { binding.amountEditor.text = it }
        initNumPad()
        initDecimalPoint()
        initBackSpace()
        initOperators()
        initEqual()
    }

    private fun initNumPad() = with(binding) {
        // For each Button representing a number
        mutableListOf(num0, num1, num2, num3, num4, num5, num6, num7, num8, num9).forEach {
            // Set listener
            it.setOnClickListener { num ->
                // Gets the number
                val numText = (num as TextView).text
                // Gets current input
                val currentText = amountEditor.text
                // If new text should override old one or the current text is zero Set editor's text to number
                amountEditor.text = if (override || currentText == "0") {
                    override = false
                    numText
                } else {
                    // If not, append the number to current text
                    "$currentText$numText"
                }
            }
        }
    }

    /**
     * Initializes decimal point functionality.
     */
    private fun initDecimalPoint() {
        // Sets listener for decimal point button
        binding.numComma.setOnClickListener {
            // Appends it if editor's current text does not contain one already.
            if (!binding.amountEditor.text.contains(Strings.get(R.string.decimal_point)/*localeComma*/)) binding.amountEditor.append(Strings.get(R.string.decimal_point)/*localeComma*/)
        }
    }

    /**
     * Initializes backspace functionality.
     */
    private fun initBackSpace() {
        with(binding) {
            // Sets listeners for backspace button
            numBackspace.setOnClickListener {
                // When pressed once and slightly
                // If editor's current text is one in length or zero substitute it with a zero
                if (amountEditor.text.toString().length == 1 && amountEditor.text != "0") amountEditor.text = "0"
                // If length is bigger than one, delete last character and set as non overridable
                if (amountEditor.text.toString().length > 1) {
                    amountEditor.text = amountEditor.text.substring(0, amountEditor.length() - 1)
                    override = false
                }
            }
            // When pressed for longer time, reset calculator
            numBackspace.setOnLongClickListener {
                binding.amountEditor.text = "0"
                reset()
            }
        }
    }

    /**
     * Resets calculator:
     * - Sets current operation to none
     * - Sets editor's current text to a zero
     * - Override is set to true, so any number pressed substitutes the current 0
     * - Memory is cleared
     *
     * @return [Boolean] always true, because it's called from [setOnLongClickListener]
     */
    private fun reset(): Boolean {
        currentOperation = Operation.NONE
        override = true
        memory = ""
        return true
    }

    /**
     * Handles what should happen if an operator is pressed.
     */
    private fun operatorPressed(operation: Operation) {
        with(binding) {
            if (memory.isNotEmpty()) doOperation()
            currentOperation = operation
            memory = amountEditor.text.toString()
            override = true
        }
    }

    /**
     * Initializes operators (sum, subs, mult, div) buttons.
     * Sets the current operation to the one indicated by the operator button.
     */
    private fun initOperators() {
        binding.opDiv.setOnClickListener { operatorPressed(Operation.DIV) }
        binding.opMult.setOnClickListener { operatorPressed(Operation.TIMES) }
        binding.opMinus.setOnClickListener { operatorPressed(Operation.MINUS) }
        binding.opPlus.setOnClickListener { operatorPressed(Operation.PLUS) }
    }

    /**
     * Initializes equal's button functionality.
     * If memory is in use or there is an operation on course,
     * [reset] is called.
     */
    private fun initEqual() {
        binding.opEqual.setOnClickListener {
            // If memory is in use (isNotEmpty) or
            // there is an operation on course, reset calculator.
            if (memory.isNotEmpty() || currentOperation != Operation.NONE) {
                doOperation()
                reset()
            }
        }
    }

    /**
     * Performs the operation previously set to current and shows result in editor.
     */
    private fun doOperation() {
        try {
            // First part of the arithmetic operation/equation: addend, minuend, factor o dividend
            val part1 = memory.replace(",", ".")/*.toDouble()*/
            // Second part: addend, subtrahend, factor o divisor
            val part2 = binding.amountEditor.text.toString().replace(",", ".")/*.toDouble()*/

            // For each operation case does expected operation in BigDecimal (to avoid floating point errors).
            // Sets the scale to fractions digits indicated by the currency provided.
            when (currentOperation) {
                Operation.PLUS -> BigDecimal(part1).plus(BigDecimal(part2)).setScale(currencyDecimalPoints, RoundingMode.DOWN)
                Operation.MINUS -> BigDecimal(part1).minus(BigDecimal(part2)).setScale(currencyDecimalPoints, RoundingMode.DOWN)
                Operation.TIMES -> BigDecimal(part1).times(BigDecimal(part2)).setScale(currencyDecimalPoints, RoundingMode.DOWN)
                Operation.DIV -> BigDecimal(part1).div(BigDecimal(part2)).setScale(currencyDecimalPoints, RoundingMode.DOWN)
                else -> throw IllegalStateException("$currentOperation provided isn't a valid operation.")
            }.also { result ->
                // Then it sets the result to editors
                binding.amountEditor.text = when {

                    // If result is lower or equal zero editor is set to "0",
                    // because negative amounts are indicated in EntryFragment with
                    // Income/Expense chips.
                    //result <= BigDecimal("0") -> "0"

                    // If result has useless decimals, such as in e.g.: 123.000 or 98.0 - Take out decimal point and zeros
                    result.hasUselessDecimals() -> result.toString().substring(0, result.toString().indexOf("."))

                    // If none of those, return result as plain text.
                    else -> result.toString()
                }
            }
        } catch (e: Exception) {
            // Sets simple error msg in editor.
            binding.amountEditor.text = Strings.get(R.string.error)
        }
    }

    /**
     * Called when calculations are done and saves the value as amount (either left or starting).
     */
    private fun done() {
        if (!isAmountError() && binding.amountEditor.text.toString().isNotEmpty()) {
            calculatorViewModel.saveAmount(binding.amountEditor.text.toString().toDouble())
            findNavController().navigateUp()
        }
    }

    /**
     * Checks whether the last operation result in an error.
     */
    private fun isAmountError(): Boolean = binding.amountEditor.text.toString() == Strings.get(R.string.error)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

