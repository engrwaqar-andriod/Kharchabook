package com.kharchabook.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_table")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val monthlyBudget: Double,
    val colorHex: String = "#1B5E20"
)