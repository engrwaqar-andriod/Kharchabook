package com.kharchabook.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kharchabook.app.data.Book
import com.kharchabook.app.data.Expense
import com.kharchabook.app.data.KharchaDatabase
import com.kharchabook.app.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    val allBooks: LiveData<List<Book>>

    // Current month string e.g. "June 2026"
    val currentMonth: String = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        .format(Date())

    init {
        val db = KharchaDatabase.getDatabase(application)
        repository = ExpenseRepository(db.expenseDao(), db.bookDao())
        allBooks = repository.allBooks
    }

    // Book functions
    fun insertBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertBook(book)
    }

    fun updateBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateBook(book)
    }

    fun deleteBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteBook(book)
    }

    // Expense functions
    fun insertExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteExpense(expense)
    }

    fun getExpensesByBookAndMonth(bookId: Int, month: String): LiveData<List<Expense>> =
        repository.getExpensesByBookAndMonth(bookId, month)

    fun getTotalByBookAndMonth(bookId: Int, month: String): LiveData<Double> =
        repository.getTotalByBookAndMonth(bookId, month)

    fun getAllExpensesByBook(bookId: Int): LiveData<List<Expense>> =
        repository.getAllExpensesByBook(bookId)

    fun getMonthsByBook(bookId: Int): LiveData<List<String>> =
        repository.getMonthsByBook(bookId)

    fun getTotalByCategoryAndMonth(
        bookId: Int, month: String, category: String
    ): LiveData<Double> =
        repository.getTotalByCategoryAndMonth(bookId, month, category)
}