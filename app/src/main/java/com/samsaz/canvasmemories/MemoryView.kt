package com.samsaz.canvasmemories

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */
class MemoryView(context: Context, attributeSet: AttributeSet? = null): View(context, attributeSet){

    val paint = Paint().apply {
        color = android.graphics.Color.RED
        isAntiAlias = true
    }
    val path = Path()
    var rect: Rect? = null
    var item = 0

    init {
        setOnClickListener {
            item = (item + 1) % 3
            invalidate()
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        when (item) {
            0 -> canvas.drawPath(configurePath(width.toFloat(), path), paint)
            1 -> canvas.drawCircle(width/2f, height/2f, width/2f, paint)
            2 -> {
                if (rect == null)
                    rect = Rect(0, 0, width, height)
                rect?.let {
                    canvas.drawRect(it, paint)
                }

            }
        }
    }

    fun getHeight(width: Double): Float {
        return Math.sqrt((Math.pow(width, 2.0) - Math.pow((width / 2), 2.0))).toFloat()
    }

    fun configurePath(sideLength: Float, path: Path): Path {
//        pointing up
        path.moveTo(0f, sideLength)
        path.lineTo(sideLength / 2f, sideLength - getHeight(sideLength.toDouble()))
        path.lineTo(sideLength, sideLength)

        return path
    }
}