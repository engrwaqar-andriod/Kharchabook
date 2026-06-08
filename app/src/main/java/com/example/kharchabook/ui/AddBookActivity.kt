package com.kharchabook.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kharchabook.app.data.Book
import com.kharchabook.app.databinding.ActivityAddBookBinding
import com.kharchabook.app.viewmodel.ExpenseViewModel

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private var editBookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check if editing existing book
        editBookId = intent.getIntExtra("BOOK_ID", -1)
        if (editBookId != -1) {
            supportActionBar?.title = "Edit Book"
            binding.etBookName.setText(intent.getStringExtra("BOOK_NAME"))
            binding.etMonthlyBudget.setText(intent.getDoubleExtra("BOOK_BUDGET", 0.0).toString())
            binding.btnSaveBook.text = "Update Book"
        } else {
            supportActionBar?.title = "Add New Book"
        }

        binding.btnSaveBook.setOnClickListener {
            val name = binding.etBookName.text.toString().trim()
            val budgetText = binding.etMonthlyBudget.text.toString().trim()

            if (name.isEmpty()) {
                binding.etBookName.error = "Please enter book name"
                return@setOnClickListener
            }

            if (budgetText.isEmpty()) {
                binding.etMonthlyBudget.error = "Please enter monthly budget"
                return@setOnClickListener
            }

            val budget = budgetText.toDoubleOrNull()
            if (budget == null || budget <= 0) {
                binding.etMonthlyBudget.error = "Please enter valid budget"
                return@setOnClickListener
            }

            if (editBookId != -1) {
                // Update existing book
                val book = Book(id = editBookId, name = name, monthlyBudget = budget)
                viewModel.updateBook(book)
                Toast.makeText(this, "Book updated!", Toast.LENGTH_SHORT).show()
            } else {
                // Create new book
                val book = Book(name = name, monthlyBudget = budget)
                viewModel.insertBook(book)
                Toast.makeText(this, "Book created!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}