package com.example.krishimitra.presentation.notification_screen

sealed class NotificationScreenEvent {

    object ClearAllNotfication: NotificationScreenEvent()

    object NotificationStatusUpdate: NotificationScreenEvent()
}