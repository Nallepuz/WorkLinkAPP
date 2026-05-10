package com.example.worklink.data.model

data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val token: String,
    val roleId: Long,
    val active: Boolean
)