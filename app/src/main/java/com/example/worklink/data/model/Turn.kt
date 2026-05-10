package com.example.worklink.data.model
data class Turn(
    val id: Long,
    val name: String,
    val description: String,
    val workStart: String?,
    val workEnd: String?,
    val colorHex: String,
    val nights: Boolean,
    val active: Boolean
)