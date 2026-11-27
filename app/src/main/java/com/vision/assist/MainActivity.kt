package com.vision.assist

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.net.Uri

class MainActivity : Activity() {
    private val REQ = 100
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
            return
        }
        val mpm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mpm.createScreenCaptureIntent(), REQ)
    }
    override fun onActivityResult(r: Int, c: Int, d: Intent?) {
        if (r == REQ && c == RESULT_OK && d != null) {
            startForegroundService(Intent(this, CaptureService::class.java)
                .putExtra("data", d))
            startForegroundService(Intent(this, OverlayService::class.java))
            finish()
        }
    }
}
