package com.example.worklink.data.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val active: Boolean,
    val rolId: Long
)