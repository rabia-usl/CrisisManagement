package com.rabiausul.crisismanagementapp.operator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable

object MarkerUtils {

    fun createColoredMarker(context: Context, color: Int, size: Int = 40): BitmapDrawable {
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)

        // Dış çember (beyaz kenar)
        val outerPaint = Paint().apply {
            this.color = android.graphics.Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, outerPaint)

        // İç daire (renkli)
        val innerPaint = Paint().apply {
            this.color = color
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, innerPaint)

        return bitmap.toDrawable(context.resources)
    }
}