package com.sdevprem.cameraxdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun addWaterMark(context: Context, imgUri: Uri, text: String) =
    withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        context.contentResolver.openInputStream(imgUri)?.use { stream ->
            //get the mutable bitmap
            bitmap = BitmapFactory.decodeStream(stream)
                .copy(Bitmap.Config.ARGB_8888, true) ?: return@use

            val canvas = Canvas(bitmap!!)

            //prepare watermark
            val watermarkPaint = Paint().apply {
                color = Color.WHITE
                textSize = 100f
                isAntiAlias = true
                alpha = 128 //adjust opacity
            }

            //determine the position
            val posX = 20f
            val posY = bitmap!!.height - watermarkPaint.textSize - 40f

            //print the watermark
            canvas.drawText(text, posX, posY, watermarkPaint)
        }

        //save the watermarked bitmap
        bitmap?.let { saveBitmap(imgUri, it, context) }
    }

suspend fun saveBitmap(uri: Uri, bitmap: Bitmap, context: Context) = withContext(Dispatchers.IO) {
    context.contentResolver.openOutputStream(uri)?.use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
    }
}
