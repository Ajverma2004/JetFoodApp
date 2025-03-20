package com.ajverma.jetfoodapp.data.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ajverma.jetfoodapp.data.network.FoodApi
import com.ajverma.jetfoodapp.domain.repositories.NotificationRepository
import com.ajverma.jetfoodapp.domain.utils.Resource
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodNotificationManager @Inject constructor(
    private val repository: NotificationRepository,
    @ApplicationContext private val context: Context
) {

    private val job = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val notificationManager = NotificationManagerCompat.from(context)

    enum class NotificationChannelType(
        val channelId: String,
        val channelName: String,
        val channelDescription: String,
        val importance: Int
    ){
        ORDER("1", "Order", "Order Notification", NotificationManager.IMPORTANCE_HIGH),
        PROMOTION("2", "Promotion", "Promotion Notification", NotificationManager.IMPORTANCE_DEFAULT),
        ACCOUNT("3", "Account", "Account Notification", NotificationManager.IMPORTANCE_LOW)
    }


    fun createChannels(){
        NotificationChannelType.entries.forEach {
            val channel = NotificationChannelCompat.Builder(it.channelId, it.importance)
                .setName(it.channelName)
                .setDescription(it.channelDescription)
                .build()

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getAndStoreToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful){
                val token = it.result
                updateToken(token)
            }
        }
    }

    fun updateToken(token: String){
        job.launch {
            val response = repository.updateToken(token)
            when(response){
                is Resource.Error -> {
                    Log.e("FoodNotificationManager", "updateToken: ${response.message}")
                }
                is Resource.Success -> {
                    Log.d("FoodNotificationManager", "updateToken: ${response.data}")
                }
            }
        }
    }


    fun showNotification(
        title: String,
        message: String,
        notificationId: Int,
        intent: PendingIntent,
        notificationChannelType: NotificationChannelType
    ){
        val notification = NotificationCompat.Builder(context, notificationChannelType.channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setAutoCancel(true)
            .setContentIntent(intent)
            .build()

        if (
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationId, notification)
        }

    }

}