package com.example.worklink.data.model

data class TurnAssignedRequest(
    val userId: Long,
    val turnId: Long,
    val date: String,
    val info: String? = null
)