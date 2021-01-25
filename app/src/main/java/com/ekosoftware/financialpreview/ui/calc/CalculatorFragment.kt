package com.ekosoftware.financialpreview.ui.calc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.core.Operation
import com.ekosoftware.financialpreview.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private var currentOperation = Operation.NONE
    private var memory = ""
    private var override = false

    private lateinit var localeComma: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localeComma = ","
        initNumPad()
        initOperators()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initNumPad() = binding.apply {
        mutableListOf(num0, num1, num2, num3, num4, num5, num6, num7, num8, num9).forEach {
            it.setOnClickListener { num ->
                val numText = (num as TextView).text
                val currentText = amountEditor.text
                amountEditor.text = if (override || currentText == "0") {
                    override = false
                    numText
                } else {
                    "$currentText$numText"
                }
            }
        }
        numComma.apply {
            text = localeComma
            setOnClickListener {
                if (!amountEditor.text.contains(localeComma)) amountEditor.append(localeComma)
            }
        }
        numBackspace.apply {
            setOnClickListener {
                if (amountEditor.text.toString().length == 1 && amountEditor.text != "0") {
                    amountEditor.text = "0"
                }
                if (amountEditor.text.toString().length > 1) {
                    amountEditor.text = amountEditor.text.substring(0, amountEditor.length() - 1)
                    override = false
                }
            }

            setOnLongClickListener {
                currentOperation = Operation.NONE
                override = true
                memory = ""
                amountEditor.text = "0"
                true
            }
        }
    }

    private fun operatorPressed(operation: Operation) = binding.apply {
        if (memory.isNotEmpty()) doOperation()
        currentOperation = operation
        memory = amountEditor.text.toString()
        override = true
    }

    private fun initOperators() = binding.apply {
        opDiv.setOnClickListener { operatorPressed(Operation.DIV) }
        opMult.setOnClickListener { operatorPressed(Operation.TIMES) }
        opMinus.setOnClickListener { operatorPressed(Operation.MINUS) }
        opPlus.setOnClickListener { operatorPressed(Operation.PLUS) }
        opEqual.setOnClickListener {
            if (memory.isNotEmpty() || currentOperation != Operation.NONE) {
                doOperation()
                memory = ""
                override = true
                currentOperation = Operation.NONE
            }
        }
    }

    private fun doOperation() = binding.apply {
        try {
            val part1 = memory.replace(",", ".").toDouble()
            val part2 = amountEditor.text.toString().replace(",", ".").toDouble()
            String.format(
                "%.2f",
                when (currentOperation) {
                    Operation.PLUS -> part1.plus(part2)
                    Operation.MINUS -> part1.minus(part2)
                    Operation.TIMES -> part1.times(part2)
                    Operation.DIV -> part1.div(part2)
                    else -> throw IllegalStateException()
                }
            ).toDouble().also { result ->
                when (result) {
                    Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN -> throw Exception()
                    else -> amountEditor.text = when {
                        result < 0 -> "0"
                        result.toInt().toDouble() == result -> result.toInt().toString()
                        else -> result.toString()
                    }
                }
            }

        } catch (e: Exception) {
            amountEditor.text = requireActivity().getString(R.string.error)
        }
    }
}

