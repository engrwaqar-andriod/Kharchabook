package com.kharchabook.app.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kharchabook.app.data.Book
import com.kharchabook.app.databinding.ItemBookBinding

class BookAdapter(
    private var books: List<Book>,
    private val currentMonth: String,
    private val onAddExpense: (Book) -> Unit,
    private val onViewExpenses: (Book) -> Unit,
    private val onEditBook: (Book) -> Unit,
    private val onDeleteBook: (Book) -> Unit,
    private val onSummary: (Book) -> Unit,
    private val onBindSpent: (Book, (Double) -> Unit) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        with(holder.binding) {

            tvBookName.text = book.name
            tvBudget.text = "Rs. ${String.format("%.0f", book.monthlyBudget)}"

            // Load spent amount from database
            onBindSpent(book) { spent ->
                val remaining = book.monthlyBudget - spent
                tvSpent.text = "Rs. ${String.format("%.0f", spent)}"
                tvRemaining.text = "Rs. ${String.format("%.0f", remaining)}"

                // Progress bar
                val progress = if (book.monthlyBudget > 0) {
                    ((spent / book.monthlyBudget) * 100).toInt().coerceIn(0, 100)
                } else 0
                progressBudget.progress = progress

                // Color remaining red if over budget
                if (remaining < 0) {
                    tvRemaining.setTextColor(Color.RED)
                    tvRemaining.text = "Rs. ${String.format("%.0f", remaining)} OVER!"
                } else {
                    tvRemaining.setTextColor(Color.parseColor("#1B5E20"))
                }
            }

            // Button clicks
            btnAddExpense.setOnClickListener { onAddExpense(book) }
            btnViewExpenses.setOnClickListener { onViewExpenses(book) }
            btnSummary.setOnClickListener { onSummary(book) }
            btnEditBook.setOnClickListener { onEditBook(book) }
            btnDeleteBook.setOnClickListener { onDeleteBook(book) }
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}