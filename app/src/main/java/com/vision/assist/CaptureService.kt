package com.vision.assist

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.projection.MediaProjectionManager
import android.os.*
import android.view.WindowManager

class CaptureService : Service() {
    private var reader: android.media.ImageReader? = null
    private var vd: android.hardware.display.VirtualDisplay? = null

    override fun onStartCommand(i: Intent?, f: Int, id: Int): Int {
        startForeground(1, notif())
        val data = i!!.getParcelableExtra<Intent>("data")!!
        val mp = (getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager)
            .getMediaProjection(data)
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val m = android.util.DisplayMetrics().also { wm.defaultDisplay.getRealMetrics(it) }
        val w = m.widthPixels / 2; val h = m.heightPixels / 2

        reader = android.media.ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 2)
        vd = mp.createVirtualDisplay("v", w, h, m.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            reader!!.surface, null, Handler(Looper.getMainLooper()))

        reader!!.setOnImageAvailableListener({ r ->
            val img = r.acquireLatestImage() ?: return@setOnImageAvailableListener
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(img.planes[0].buffer)
            img.close()
            YoloDetector.detect(bmp) { boxes -> OverlayView.push(boxes, w, h) }
        }, Handler(Looper.getMainLooper()))
        return START_STICKY
    }
    private fun notif(): Notification {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel("c","c",NotificationManager.IMPORTANCE_LOW))
        return Notification.Builder(this,"c").setSmallIcon(android.R.drawable.ic_menu_camera).build()
    }
}
