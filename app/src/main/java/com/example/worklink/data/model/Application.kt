package com.example.worklink.data.model

data class Application(
    val id: Long,
    val status: String,
    val applicationTypeId: Long,
    val userId: Long,
    val created: String,
    val comments: String,
    val resolved: String?,
    val userName: String? = null,
    val affectedUserId: Long? = null,
    val affectedUserName: String? = null,
    val resolverComments: String? = null
)