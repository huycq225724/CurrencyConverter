package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var tvRateInfo: TextView
    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.876,
        "GBP" to 0.762,
        "JPY" to 153.465,
        "AUD" to 1.542,
        "CAD" to 1.412,
        "CHF" to 0.807,
        "CNY" to 7.123,
        "SGD" to 1.303,
        "VND" to 26305.0
    )
    private var isUpdating = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        etFrom = findViewById(R.id.etFrom)
        etTo = findViewById(R.id.etTo)
        tvRateInfo = findViewById(R.id.tvRateInfo)

        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.setSelection(0)
        spinnerTo.setSelection(1)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true


                val fromCurrency = spinnerFrom.selectedItem.toString()
                val toCurrency = spinnerTo.selectedItem.toString()
                val input = s.toString().toDoubleOrNull() ?: 0.0
                val result = convertCurrency(input, fromCurrency, toCurrency)

                if (etFrom.hasFocus()) {
                    etTo.setText(String.format("%.2f", result))
                } else if (etTo.hasFocus()) {
                    val reverse = convertCurrency(input, toCurrency, fromCurrency)
                    etFrom.setText(String.format("%.2f", reverse))
                }

                tvRateInfo.text = "1 $fromCurrency = ${String.format("%.2f", convertCurrency(1.0, fromCurrency, toCurrency))} $toCurrency"

                isUpdating = false
            }
        }

        etFrom.addTextChangedListener(textWatcher)
        etTo.addTextChangedListener(textWatcher)

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                etFrom.text?.let { textWatcher.onTextChanged(it, 0, 0, 0) }
            }
        }
        spinnerFrom.onItemSelectedListener = listener
        spinnerTo.onItemSelectedListener = listener
    }
    private fun convertCurrency(amount: Double, from: String, to: String): Double {
        val rateFrom = exchangeRates[from] ?: 1.0
        val rateTo = exchangeRates[to] ?: 1.0
        return amount * (rateTo / rateFrom)
    }
}
