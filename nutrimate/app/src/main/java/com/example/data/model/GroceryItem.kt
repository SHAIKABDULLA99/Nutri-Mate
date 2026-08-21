package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameEn: String,
    val nameTe: String,
    val categoryEn: String,
    val categoryTe: String,
    val quantity: String,
    val isBought: Boolean = false,
    val isHealthyChoice: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
