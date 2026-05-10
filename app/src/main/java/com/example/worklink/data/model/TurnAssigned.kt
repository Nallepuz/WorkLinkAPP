package com.example.worklink.data.model

data class TurnAssigned(
    val id: Long,
    val date: String,
    val created: String?,
    val info: String?,
    val userId: Long,
    val userName: String,
    val turnId: Long,
    val turnName: String
)