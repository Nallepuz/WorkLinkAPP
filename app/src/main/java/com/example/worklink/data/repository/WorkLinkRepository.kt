package com.example.worklink.data.repository

import com.example.worklink.data.api.RetrofitClient
import com.example.worklink.data.model.*

class WorkLinkRepository {

    private val api = RetrofitClient.instance

    // Users
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(token: String): Result<List<User>> {
        return try {
            val users = api.getUsers("Bearer $token")
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByEmail(token: String, email: String): Result<User> {
        return try {
            val users = api.getUserByEmail("Bearer $token", email)
            if (users.isNotEmpty()) Result.success(users.first())
            else Result.failure(Exception("Usuario no encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(token: String, userId: Long, name: String, email: String, phone: String, password: String, rolId: Long): Result<User> {
        return try {
            val request = UpdateUserRequest(
                name = name,
                email = email,
                password = password,
                phone = phone,
                rolId = rolId
            )
            val user = api.updateUser("Bearer $token", userId, request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Turns
    suspend fun getTurns(token: String): Result<List<Turn>> {
        return try {
            val turns = api.getTurns("Bearer $token")
            Result.success(turns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAssignedTurns(token: String, userId: Long): Result<List<TurnAssigned>> {
        return try {
            val turns = api.getAssignedTurns("Bearer $token", userId)
            Result.success(turns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Applications
    suspend fun getAllApplications(token: String): Result<List<Application>> {
        return try {
            val applications = api.getAllApplications("Bearer $token")
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApplicationDays(token: String, id: Long): Result<ApplicationDaysDetail> {
        return try {
            Result.success(api.getApplicationDays("Bearer $token", id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApplicationHours(token: String, id: Long): Result<ApplicationHoursDetail> {
        return try {
            Result.success(api.getApplicationHours("Bearer $token", id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApplicationChange(token: String, id: Long): Result<ApplicationChangeDetail> {
        return try {
            Result.success(api.getApplicationChange("Bearer $token", id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApplicationsByUser(token: String, userId: Long): Result<List<Application>> {
        return try {
            val applications = api.getApplicationsByUser("Bearer $token", userId)
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createApplication(token: String, request: ApplicationRequest): Result<Application> {
        return try {
            val application = api.createApplication("Bearer $token", request)
            Result.success(application)
        } catch (e: Exception) {
            val errorMessage = if (e is retrofit2.HttpException) {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    org.json.JSONObject(errorBody ?: "").optString("message", "Error al enviar la solicitud")
                } catch (ex: Exception) {
                    "Error al enviar la solicitud"
                }
            } else e.message ?: "Error al enviar la solicitud"
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun resolveApplication(token: String, applicationId: Long, approved: Boolean, resolverId: Long): Result<Application> {
        return try {
            val application = api.getAllApplications("Bearer $token")
                .first { it.id == applicationId }

            val request = ResolveApplicationRequest(
                applicationTypeId = application.applicationTypeId,
                userId = application.userId,
                comments = application.comments,
                status = if (approved) "APPROVED" else "REJECTED",
                resolverId = resolverId,
                resolverComments = if (approved) "Solicitud aprobada" else "Solicitud rechazada"
            )
            val result = api.resolveApplication("Bearer $token", applicationId, request)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApplicationTypes(token: String): Result<List<ApplicationType>> {
        return try {
            val types = api.getApplicationTypes("Bearer $token")
            Result.success(types)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBalance(token: String, userId: Long): Result<List<UserBalance>> {
        return try {
            val balance = api.getUserBalance("Bearer $token", userId)
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Notificaciones
    suspend fun getNotifications(token: String, userId: Long): Result<List<Notification>> {
        return try {
            val notifications = api.getNotifications("Bearer $token", userId)
            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadCount(token: String, userId: Long): Result<Long> {
        return try {
            val response = api.getUnreadCount("Bearer $token", userId)
            Result.success(response["unread"] ?: 0L)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllAsRead(token: String, userId: Long): Result<Unit> {
        return try {
            api.markAllAsRead("Bearer $token", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAssigned(token: String, userId: Long, turnId: Long, date: String): Result<TurnAssigned> {
        return try {
            val request = TurnAssignedRequest(userId = userId, turnId = turnId, date = date)
            val result = api.createAssigned("Bearer $token", request)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAssigned(token: String, id: Long, userId: Long, turnId: Long, date: String): Result<TurnAssigned> {
        return try {
            val request = TurnAssignedRequest(userId = userId, turnId = turnId, date = date)
            val result = api.updateAssigned("Bearer $token", id, request)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAssigned(token: String, id: Long): Result<Unit> {
        return try {
            api.deleteAssigned("Bearer $token", id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAnnouncement(token: String, message: String): Result<Unit> {
        return try {
            api.createAnnouncement("Bearer $token", mapOf("message" to message))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotificationById(token: String, id: Long): Result<Unit> {
        return try {
            api.deleteNotificationById("Bearer $token", id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllReadNotifications(token: String, userId: Long): Result<Unit> {
        return try {
            api.deleteAllReadNotifications("Bearer $token", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}