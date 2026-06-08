package com.kharchabook.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM book_table ORDER BY id ASC")
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM book_table WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): Book?
}