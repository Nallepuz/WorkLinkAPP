package com.example.worklink.data.api

import com.example.worklink.data.model.*
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Users
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): List<User>

    @GET("users")
    suspend fun getUserByEmail(
        @Header("Authorization") token: String,
        @Query("email") email: String
    ): List<User>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Long,
        @Body request: UpdateUserRequest
    ): User

    // Turnos
    @GET("turns")
    suspend fun getTurns(
        @Header("Authorization") token: String
    ): List<Turn>

    // Turnos asignados
    @GET("assigned")
    suspend fun getAssignedTurns(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Long
    ): List<TurnAssigned>

    @POST("assigned")
    suspend fun createAssigned(
        @Header("Authorization") token: String,
        @Body request: TurnAssignedRequest
    ): TurnAssigned

    @PUT("assigned/{id}")
    suspend fun updateAssigned(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body request: TurnAssignedRequest
    ): TurnAssigned

    @DELETE("assigned/{id}")
    suspend fun deleteAssigned(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    )

    // Solicitudes
    @GET("application")
    suspend fun getAllApplications(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): List<Application>

    @GET("application/days/{id}")
    suspend fun getApplicationDays(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApplicationDaysDetail

    @GET("application/hours/{id}")
    suspend fun getApplicationHours(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApplicationHoursDetail

    @GET("application/change/{id}")
    suspend fun getApplicationChange(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApplicationChangeDetail

    @GET("application/user/{userId}")
    suspend fun getApplicationsByUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): List<Application>

    @POST("application")
    suspend fun createApplication(
        @Header("Authorization") token: String,
        @Body request: ApplicationRequest
    ): Application

    @PUT("application/{id}")
    suspend fun resolveApplication(
        @Header("Authorization") token: String,
        @Path("id") applicationId: Long,
        @Body request: ResolveApplicationRequest
    ): Application

    // Tipos de solicitud
    @GET("applicationType")
    suspend fun getApplicationTypes(
        @Header("Authorization") token: String
    ): List<ApplicationType>

    // Balance
    @GET("userBalances")
    suspend fun getUserBalance(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Long
    ): List<UserBalance>

    // Notificaciones
    @GET("notifications/{userId}")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): List<Notification>

    @GET("notifications/{userId}/unread")
    suspend fun getUnreadNotifications(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): List<Notification>

    @GET("notifications/{userId}/count")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): Map<String, Long>

    @PUT("notifications/{userId}/read")
    suspend fun markAllAsRead(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    )

    @POST("notifications/announcement")
    suspend fun createAnnouncement(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    )

    @DELETE("notifications/single/{id}")
    suspend fun deleteNotificationById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    )

    @DELETE("notifications/{userId}/read")
    suspend fun deleteAllReadNotifications(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    )
}