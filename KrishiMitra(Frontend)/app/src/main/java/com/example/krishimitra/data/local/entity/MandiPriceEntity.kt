package com.example.krishimitra.data.local.entity

import androidx.room.Entity


@Entity(
    tableName = "mandi_data",
    primaryKeys = ["market","commodity","arrival_date"]
)
data class MandiPriceEntity(
    val arrival_date: String = "",
    val commodity: String = "",
    val district: String = "",
    val grade: String = "",
    val market: String = "",
    val max_price: String = "",
    val min_price: String = "",
    val modal_price: String = "",
    val state: String = "",
    val variety: String = ""
)
