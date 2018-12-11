package com.samsaz.canvasmemories.ui

import android.graphics.*
import androidx.annotation.ColorInt

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

abstract class Drawer(internal val size: Int, @ColorInt internal val color: Int) {
    internal val paint = Paint().apply {
        color = this@Drawer.color
        isAntiAlias = true
    }

    abstract fun draw(canvas: Canvas)
}

class SquareDrawer(size: Int): Drawer(size, Color.BLUE) {

    private var rect: Rect? = null

    override fun draw(canvas: Canvas) {
        if (rect == null)
            rect = Rect(0, 0, size, size)
        rect?.let {
            canvas.drawRect(it, paint)
        }
    }
}

class CircleDrawer(size: Int): Drawer(size, Color.RED) {

    override fun draw(canvas: Canvas) {
        val halfSize = size/2f
        canvas.drawCircle(halfSize, halfSize, halfSize, paint)
    }
}

class TriangleDrawer(size: Int): Drawer(size, Color.GREEN) {
    private val height by lazy(LazyThreadSafetyMode.NONE) {
        val side = size.toDouble()
        Math.sqrt((Math.pow(side, 2.0) - Math.pow((side / 2), 2.0))).toFloat()
    }
    private val path = Path()

    override fun draw(canvas: Canvas) {
        val side = size.toFloat()
        canvas.drawPath(configurePath(side, path), paint)
    }

    private fun configurePath(sideLength: Float, path: Path): Path {
        path.moveTo(0f, sideLength)
        path.lineTo(sideLength / 2f, sideLength - height)
        path.lineTo(sideLength, sideLength)

        return path
    }
}