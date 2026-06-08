package com.kharchabook.app.repository

import androidx.lifecycle.LiveData
import com.kharchabook.app.data.Book
import com.kharchabook.app.data.BookDao
import com.kharchabook.app.data.Expense
import com.kharchabook.app.data.ExpenseDao

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val bookDao: BookDao
) {

    // Book operations
    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

    suspend fun insertBook(book: Book) = bookDao.insertBook(book)
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    suspend fun getBookById(bookId: Int) = bookDao.getBookById(bookId)

    // Expense operations
    fun getExpensesByBookAndMonth(bookId: Int, month: String): LiveData<List<Expense>> =
        expenseDao.getExpensesByBookAndMonth(bookId, month)

    fun getTotalByBookAndMonth(bookId: Int, month: String): LiveData<Double> =
        expenseDao.getTotalByBookAndMonth(bookId, month)

    fun getAllExpensesByBook(bookId: Int): LiveData<List<Expense>> =
        expenseDao.getAllExpensesByBook(bookId)

    fun getMonthsByBook(bookId: Int): LiveData<List<String>> =
        expenseDao.getMonthsByBook(bookId)

    fun getTotalByCategoryAndMonth(bookId: Int, month: String, category: String): LiveData<Double> =
        expenseDao.getTotalByCategoryAndMonth(bookId, month, category)

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}