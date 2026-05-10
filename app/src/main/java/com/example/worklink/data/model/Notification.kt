package com.example.worklink.data.model

data class Notification(
    val id: Long,
    val userId: Long,
    val message: String,
    val read: Boolean,
    val type: String = "PERSONAL",
    val createdAt: String
)