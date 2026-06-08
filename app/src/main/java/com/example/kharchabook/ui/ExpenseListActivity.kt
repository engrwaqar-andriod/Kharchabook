package com.kharchabook.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kharchabook.app.R
import com.kharchabook.app.databinding.ActivityExpenseListBinding
import com.kharchabook.app.viewmodel.ExpenseViewModel

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter
    private var bookId: Int = -1
    private var bookName: String = ""
    private var selectedMonth: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getIntExtra("BOOK_ID", -1)
        bookName = intent.getStringExtra("BOOK_NAME") ?: "Expenses"
        selectedMonth = viewModel.currentMonth

        supportActionBar?.title = bookName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupMonthSpinner()
        observeExpenses()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(
            expenses = emptyList(),
            onEditClick = { expense ->
                val intent = Intent(this, AddExpenseActivity::class.java)
                intent.putExtra("BOOK_ID", bookId)
                intent.putExtra("BOOK_NAME", bookName)
                intent.putExtra("EXPENSE_ID", expense.id)
                intent.putExtra("EXPENSE_AMOUNT", expense.amount)
                intent.putExtra("EXPENSE_CATEGORY", expense.category)
                intent.putExtra("EXPENSE_NOTE", expense.note)
                intent.putExtra("EXPENSE_DATE", expense.date)
                startActivity(intent)
            },
            onDeleteClick = { expense ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Expense")
                    .setMessage("Delete this expense of Rs. ${
                        String.format("%.0f", expense.amount)
                    }?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteExpense(expense)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = adapter
    }

    private fun setupMonthSpinner() {
        viewModel.getMonthsByBook(bookId).observe(this) { months ->
            val monthList = months.toMutableList()
            if (!monthList.contains(viewModel.currentMonth)) {
                monthList.add(0, viewModel.currentMonth)
            }

            val spinnerAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                monthList
            )
            spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            binding.spinnerMonth.adapter = spinnerAdapter

            binding.spinnerMonth.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View?, pos: Int, id: Long
                    ) {
                        selectedMonth = monthList[pos]
                        observeExpenses()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
        }
    }

    private fun observeExpenses() {
        viewModel.getExpensesByBookAndMonth(bookId, selectedMonth)
            .observe(this) { expenses ->
                adapter.updateExpenses(expenses)
            }

        viewModel.getTotalByBookAndMonth(bookId, selectedMonth)
            .observe(this) { total ->
                val amount = total ?: 0.0
                binding.tvListTotal.text =
                    "Total: Rs. ${String.format("%.0f", amount)}"
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}