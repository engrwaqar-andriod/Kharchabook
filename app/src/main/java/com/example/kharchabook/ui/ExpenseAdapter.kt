package com.kharchabook.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kharchabook.app.data.Expense
import com.kharchabook.app.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onEditClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        with(holder.binding) {
            tvCategory.text = expense.category
            tvNote.text = if (expense.note.isEmpty()) "No note" else expense.note
            tvDate.text = expense.date
            tvAmount.text = "Rs. ${String.format("%.0f", expense.amount)}"
            tvCategoryIcon.text = getCategoryEmoji(expense.category)

            // Single tap to edit
            root.setOnClickListener { onEditClick(expense) }

            // Long press to delete
            root.setOnLongClickListener {
                onDeleteClick(expense)
                true
            }
        }
    }

    override fun getItemCount() = expenses.size

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    private fun getCategoryEmoji(category: String): String {
        return when (category) {
            "Food" -> "🍔"
            "Transport" -> "🚗"
            "Bills" -> "💡"
            "Shopping" -> "🛍️"
            "Health" -> "💊"
            "Education" -> "📚"
            else -> "💰"
        }
    }
}