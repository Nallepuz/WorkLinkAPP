package com.example.worklink.data.model

data class ResolveApplicationRequest(
    val applicationTypeId: Long,
    val userId: Long,
    val comments: String,
    val status: String,
    val resolverId: Long,
    val resolverComments: String
)