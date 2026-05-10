package com.example.worklink.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.worklink.R

object NotificationHelper {

    private const val CHANNEL_ID_REQUESTS = "worklink_requests"
    private const val CHANNEL_ID_ADMIN = "worklink_admin"

    // Crear canales de notificación para Android
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val requestsChannel = NotificationChannel(
                CHANNEL_ID_REQUESTS,
                "Solicitudes",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre el estado de tus solicitudes"
            }

            val adminChannel = NotificationChannel(
                CHANNEL_ID_ADMIN,
                "Administración",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Nuevas solicitudes pendientes de aprobación"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(requestsChannel)
            manager.createNotificationChannel(adminChannel)
        }
    }

    // Mostrar notificaciónes
    fun notifyRequestStatusChanged(
        context: Context,
        requestId: Long,
        status: String
    ) {
        val title = when (status) {
            "APPROVED" -> "✅ Solicitud aprobada"
            "REJECTED" -> "❌ Solicitud rechazada"
            else -> "📋 Solicitud actualizada"
        }
        val message = when (status) {
            "APPROVED" -> "Tu solicitud #$requestId ha sido aprobada"
            "REJECTED" -> "Tu solicitud #$requestId ha sido rechazada"
            else -> "Tu solicitud #$requestId ha sido actualizada"
        }

        sendNotification(context, CHANNEL_ID_REQUESTS, requestId.toInt(), title, message)
    }

    fun notifyAdminNewRequest(
        context: Context,
        requestId: Long,
        typeName: String
    ) {
        sendNotification(
            context = context,
            channelId = CHANNEL_ID_ADMIN,
            notificationId = (requestId + 1000).toInt(),
            title = "📬 Nueva solicitud pendiente",
            message = "Solicitud de $typeName #$requestId esperando aprobación"
        )
    }

    // Enviar notificación
    private fun sendNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        message: String
    ) {
        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permiso no concedido
        }
    }
}