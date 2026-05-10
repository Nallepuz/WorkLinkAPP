package com.example.worklink.data.model

data class UpdateUserRequest(
    val name: String,
    val email: String,
    val password: String = "unchanged",
    val phone: String,
    val active: Boolean = true,
    val rolId: Long
)