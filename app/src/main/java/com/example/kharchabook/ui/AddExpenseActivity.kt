package com.kharchabook.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kharchabook.app.data.Expense
import com.kharchabook.app.databinding.ActivityAddExpenseBinding
import com.kharchabook.app.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private var selectedDate = ""
    private var bookId: Int = -1
    private var editExpenseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get book info from intent
        bookId = intent.getIntExtra("BOOK_ID", -1)
        val bookName = intent.getStringExtra("BOOK_NAME") ?: "Expenses"
        supportActionBar?.title = "Add to $bookName"

        // Check if editing
        editExpenseId = intent.getIntExtra("EXPENSE_ID", -1)
        if (editExpenseId != -1) {
            supportActionBar?.title = "Edit Expense"
            binding.etAmount.setText(intent.getDoubleExtra("EXPENSE_AMOUNT", 0.0).toString())
            binding.etNote.setText(intent.getStringExtra("EXPENSE_NOTE") ?: "")
            selectedDate = intent.getStringExtra("EXPENSE_DATE") ?: ""
            binding.tvDate.text = selectedDate
            binding.btnSaveExpense.text = "Update Expense"
        }

        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val categories = listOf(
            "Food", "Transport", "Bills",
            "Shopping", "Health", "Education", "Other"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Set category if editing
        if (editExpenseId != -1) {
            val category = intent.getStringExtra("EXPENSE_CATEGORY") ?: "Food"
            val position = categories.indexOf(category)
            if (position >= 0) binding.spinnerCategory.setSelection(position)
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        if (selectedDate.isEmpty()) {
            selectedDate = dateFormat.format(calendar.time)
            binding.tvDate.text = selectedDate
        }

        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDate = dateFormat.format(calendar.time)
                    binding.tvDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            val amountText = binding.etAmount.text.toString().trim()
            val note = binding.etNote.text.toString().trim()
            val category = binding.spinnerCategory.selectedItem.toString()

            if (amountText.isEmpty()) {
                binding.etAmount.error = "Please enter amount"
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.etAmount.error = "Please enter valid amount"
                return@setOnClickListener
            }

            if (bookId == -1) {
                Toast.makeText(this, "Error: No book selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expense = Expense(
                id = if (editExpenseId != -1) editExpenseId else 0,
                bookId = bookId,
                amount = amount,
                category = category,
                note = note,
                date = selectedDate,
                month = viewModel.currentMonth
            )

            if (editExpenseId != -1) {
                viewModel.updateExpense(expense)
                Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertExpense(expense)
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}