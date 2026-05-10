package com.example.worklink.data.model

data class UserBalance(
    val id: Long,
    val userId: Long,
    val userName: String,
    val year: Int,
    val vacationDays: Int,
    val excessDays: Int,
    val unpaidDays: Int,
    val hoursBalance: Float
)