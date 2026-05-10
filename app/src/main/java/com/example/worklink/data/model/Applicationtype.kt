package com.example.worklink.data.model

data class ApplicationType(
    val id: Long,
    val name: String,
    val description: String,
    val affectsBalance: Boolean,
    val active: Boolean
)