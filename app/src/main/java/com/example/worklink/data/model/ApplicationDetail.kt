package com.example.worklink.data.model

data class ApplicationDaysDetail(
    val id: Long,
    val status: String,
    val applicationTypeId: Long,
    val userId: Long,
    val created: String,
    val comments: String,
    val startDate: String,
    val endDate: String,
    val resolverId: Long?,
    val resolved: String?,
    val resolverComments: String?
)

data class ApplicationHoursDetail(
    val id: Long,
    val status: String,
    val applicationTypeId: Long,
    val userId: Long,
    val created: String,
    val comments: String,
    val hoursRequested: Float,
    val date: String,
    val fromTime: String,
    val toTime: String,
    val resolverId: Long?,
    val resolved: String?,
    val resolverComments: String?
)

data class ApplicationChangeDetail(
    val id: Long,
    val status: String,
    val applicationTypeId: Long,
    val userId: Long,
    val created: String,
    val comments: String,
    val startDate: String,
    val endDate: String,
    val affectedUserId: Long,
    val turnGiveId: Long,
    val turnReceiveId: Long,
    val resolverId: Long?,
    val resolved: String?,
    val resolverComments: String?
)