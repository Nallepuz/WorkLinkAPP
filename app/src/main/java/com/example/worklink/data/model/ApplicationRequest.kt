package com.example.worklink.data.model

data class ApplicationRequest(
    val applicationTypeId: Long,
    val userId: Long,
    val comments: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val date: String? = null,
    val fromTime: String? = null,
    val toTime: String? = null,
    val hoursRequested: Float? = null,
    val affectedUserId: Long? = null,
    val turnGiveId: Long? = null,
    val turnReceiveId: Long? = null
)