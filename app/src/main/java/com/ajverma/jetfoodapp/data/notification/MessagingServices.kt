package com.ajverma.jetfoodapp.data.notification

import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajverma.jetfoodapp.MainActivity
import com.ajverma.jetfoodapp.data.network.models.notificationModels.Notification
import com.ajverma.jetfoodapp.presentation.screens.notifications.NotificationsViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MessagingServices: FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: FoodNotificationManager


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        notificationManager.updateToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val title = message.notification?.title ?: ""
        val body = message.notification?.body ?: ""
        val data = message.data
        val type = data["type"] ?: "general"

        if (type == "order"){
            val orderId = data[ORDER_ID]
            intent.putExtra(ORDER_ID, orderId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationChannelType = when(type){
            "order" -> FoodNotificationManager.NotificationChannelType.ORDER
            "general" -> FoodNotificationManager.NotificationChannelType.PROMOTION
            else -> FoodNotificationManager.NotificationChannelType.ACCOUNT
        }


        notificationManager.showNotification(
            title,
            body,
            1,
            pendingIntent,
            notificationChannelType
        )
    }

    companion object {
        const val ORDER_ID = "orderId"
    }
}