package com.vision.assist

import android.app.*
import android.graphics.PixelFormat
import android.os.*
import android.view.*

class OverlayService : Service() {
    override fun onStartCommand(i: Intent?, f: Int, id: Int): Int {
        startForeground(2, notif())
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_SECURE,
            PixelFormat.TRANSLUCENT)
        wm.addView(OverlayView(this), lp)
        return START_STICKY
    }
    private fun notif(): Notification {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel("c2","c2",NotificationManager.IMPORTANCE_LOW))
        return Notification.Builder(this,"c2").setSmallIcon(android.R.drawable.ic_menu_edit).build()
    }
}
