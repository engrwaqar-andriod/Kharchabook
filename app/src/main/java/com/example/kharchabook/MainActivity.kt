package com.kharchabook.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kharchabook.app.databinding.ActivityMainBinding
import com.kharchabook.app.ui.AddBookActivity
import com.kharchabook.app.ui.AddExpenseActivity
import com.kharchabook.app.ui.BookAdapter
import com.kharchabook.app.ui.ExpenseListActivity
import com.kharchabook.app.ui.SummaryActivity
import com.kharchabook.app.viewmodel.ExpenseViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = ContextCompat.getColor(this, R.color.light_gray)

        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(16, systemBars.top + 16, 16, 16)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.rvBooks) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(12, 12, 12, systemBars.bottom + 12)
            insets
        }

        binding.tvCurrentMonth.text = viewModel.currentMonth
        setupRecyclerView()
        observeBooks()

        binding.btnAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }

        binding.btnEmptyAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }

    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            books = emptyList(),
            currentMonth = viewModel.currentMonth,
            onAddExpense = { book ->
                val intent = Intent(this, AddExpenseActivity::class.java)
                intent.putExtra("BOOK_ID", book.id)
                intent.putExtra("BOOK_NAME", book.name)
                startActivity(intent)
            },
            onViewExpenses = { book ->
                val intent = Intent(this, ExpenseListActivity::class.java)
                intent.putExtra("BOOK_ID", book.id)
                intent.putExtra("BOOK_NAME", book.name)
                startActivity(intent)
            },
            onEditBook = { book ->
                val intent = Intent(this, AddBookActivity::class.java)
                intent.putExtra("BOOK_ID", book.id)
                intent.putExtra("BOOK_NAME", book.name)
                intent.putExtra("BOOK_BUDGET", book.monthlyBudget)
                startActivity(intent)
            },
            onDeleteBook = { book ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Book")
                    .setMessage("Delete '${book.name}'? All expenses in this book will also be deleted.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteBook(book)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onSummary = { book ->
                val intent = Intent(this, SummaryActivity::class.java)
                intent.putExtra("BOOK_ID", book.id)
                intent.putExtra("BOOK_NAME", book.name)
                startActivity(intent)
            },
            onBindSpent = { book, callback ->
                viewModel.getTotalByBookAndMonth(book.id, viewModel.currentMonth)
                    .observe(this) { total ->
                        callback(total ?: 0.0)
                    }
            }
        )

        binding.rvBooks.layoutManager = LinearLayoutManager(this)
        binding.rvBooks.adapter = bookAdapter
    }

    private fun observeBooks() {
        viewModel.allBooks.observe(this) { books ->
            bookAdapter.updateBooks(books)

            // Show empty state when no books
            if (books.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvBooks.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvBooks.visibility = View.VISIBLE
            }
        }
    }
}