package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery_items ORDER BY isBought ASC, timestamp DESC")
    fun getAllGroceryItems(): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GroceryItem>)

    @Update
    suspend fun updateGroceryItem(item: GroceryItem)

    @Query("UPDATE grocery_items SET isBought = :isBought WHERE id = :id")
    suspend fun toggleBought(id: Long, isBought: Boolean)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteGroceryItem(id: Long)

    @Query("DELETE FROM grocery_items WHERE isBought = 1")
    suspend fun clearCompleted()
}
