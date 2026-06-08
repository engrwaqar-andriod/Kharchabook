package com.kharchabook.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: Int,
    val amount: Double,
    val category: String,
    val note: String,
    val date: String,
    val month: String
)