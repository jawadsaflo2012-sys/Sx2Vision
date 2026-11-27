package com.vision.assist

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

object YoloDetector {
    private const val IN = 640
    private var tflite: Interpreter? = null
    @Volatile var enabled = false

    fun init(assetMgr: android.content.res.AssetManager) {
        if (tflite != null) return
        val opts = Interpreter.Options().apply { setNumThreads(4) }
        tflite = Interpreter(assetMgr.open("model.tflite").readBytes().let {
            java.nio.channels.Channels.newChannel(assetMgr.open("model.tflite")).let { ch ->
                val bb = ByteBuffer.allocateDirect(20_000_000).order(ByteOrder.nativeOrder())
                val c = java.nio.channels.Channels.newChannel(assetMgr.open("model.tflite"))
                while (true) { val r = c.read(bb); if (r < 0) break; }
                bb.flip(); bb
            }
        }, opts)
    }

    fun detect(bmp: Bitmap, cb: (List<floatArrayOf2>) -> Unit) {
        if (!enabled || tflite == null) return
        val scaled = Bitmap.createScaledBitmap(bmp, IN, IN, true)
        val px = FloatArray(IN*IN*3)
        val pix = IntArray(IN*IN)
        scaled.getPixels(pix, 0, IN, 0, 0, IN, IN)
        for (i in pix.indices) {
            px[i]      = ((pix[i] shr 16 and 0xFF)/255f)
            px[IN*IN+i]= ((pix[i] shr 8 and 0xFF)/255f)
            px[2*IN*IN+i]=((pix[i] and 0xFF)/255f)
        }
        val input = ByteBuffer.allocateDirect(4*IN*IN*3).order(ByteOrder.nativeOrder())
        for (v in px) input.putFloat(v); input.rewind()
        val out = Array(1){ FloatArray(6) } // simplified single-class output
        try { tflite!!.run(input, out) } catch (e: Exception) { return }
        val boxes = ArrayList<Pair<android.graphics.RectF, Float>>()
        val a = out[0]
        if (a[4] > 0.45f) {
            val cx=a[0]*IN; val cy=a[1]*IN; val w=a[2]*IN; val h=a[3]*IN
            boxes.add(Pair(android.graphics.RectF(cx-w/2, cy-h/2, cx+w/2, cy+h/2), a[4]))
        }
        cb(boxes)
    }
}
typealias floatArrayOf2 = Pair<android.graphics.RectF, Float>
