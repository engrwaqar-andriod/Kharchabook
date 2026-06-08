package com.kharchabook.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE bookId = :bookId AND month = :month ORDER BY id DESC")
    fun getExpensesByBookAndMonth(bookId: Int, month: String): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expense_table WHERE bookId = :bookId AND month = :month")
    fun getTotalByBookAndMonth(bookId: Int, month: String): LiveData<Double>

    @Query("SELECT * FROM expense_table WHERE bookId = :bookId ORDER BY id DESC")
    fun getAllExpensesByBook(bookId: Int): LiveData<List<Expense>>

    @Query("SELECT DISTINCT month FROM expense_table WHERE bookId = :bookId ORDER BY id DESC")
    fun getMonthsByBook(bookId: Int): LiveData<List<String>>

    @Query("SELECT SUM(amount) FROM expense_table WHERE bookId = :bookId AND month = :month AND category = :category")
    fun getTotalByCategoryAndMonth(bookId: Int, month: String, category: String): LiveData<Double>
}