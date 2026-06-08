package com.kharchabook.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.kharchabook.app.R
import com.kharchabook.app.databinding.ActivitySummaryBinding
import com.kharchabook.app.viewmodel.ExpenseViewModel

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding
    private val viewModel: ExpenseViewModel by viewModels()

    private val categories = listOf(
        "Food", "Transport", "Bills",
        "Shopping", "Health", "Education", "Other"
    )

    private var bookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getIntExtra("BOOK_ID", -1)
        val bookName = intent.getStringExtra("BOOK_NAME") ?: "Summary"

        supportActionBar?.title = "$bookName - Summary"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeTotalExpense()
        loadCategoryData()
    }

    private fun observeTotalExpense() {
        viewModel.getTotalByBookAndMonth(bookId, viewModel.currentMonth)
            .observe(this) { total ->
                val amount = total ?: 0.0
                binding.tvSummaryTotal.text =
                    "Rs. ${String.format("%.0f", amount)}"
            }
    }

    private fun loadCategoryData() {
        val categoryTotals = mutableMapOf<String, Double>()
        var loadedCount = 0

        categories.forEach { category ->
            viewModel.getTotalByCategoryAndMonth(
                bookId, viewModel.currentMonth, category
            ).observe(this) { total ->
                categoryTotals[category] = total ?: 0.0
                loadedCount++
                if (loadedCount == categories.size) {
                    updatePieChart(categoryTotals)
                    updateCategoryBreakdown(categoryTotals)
                }
            }
        }
    }

    private fun updatePieChart(categoryTotals: Map<String, Double>) {
        val entries = categoryTotals
            .filter { it.value > 0 }
            .map { PieEntry(it.value.toFloat(), it.key) }

        if (entries.isEmpty()) return

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)

        with(binding.pieChart) {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.WHITE)
            centerText = "Kharcha"
            setCenterTextSize(14f)
            animateY(1000)
            invalidate()
        }
    }
    private fun updateCategoryBreakdown(categoryTotals: Map<String, Double>) {
        binding.llCategoryBreakdown.removeAllViews()

        categoryTotals
            .filter { it.value > 0 }
            .forEach { (category, total) ->
                val itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_category_row,
                        binding.llCategoryBreakdown, false)

                itemView.findViewById<TextView>(R.id.tvCatName).text = category
                itemView.findViewById<TextView>(R.id.tvCatAmount).text =
                    "Rs. ${String.format("%.0f", total)}"

                binding.llCategoryBreakdown.addView(itemView)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}