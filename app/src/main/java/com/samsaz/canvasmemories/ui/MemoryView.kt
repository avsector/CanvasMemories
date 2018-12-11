package com.samsaz.canvasmemories.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryState
import com.samsaz.canvasmemories.model.MemoryType

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */
class MemoryView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null,
                                           var memory: Memory? = null): View(context, attributeSet){

    val paint = Paint().apply {
        color = android.graphics.Color.RED
        isAntiAlias = true
    }
    val path = Path()
    var rect: Rect? = null
    var eventListener: ((MemoryEvent) -> Unit)? = null

    init {
        setOnClickListener {
            memory?.let {
                eventListener?.invoke(MemoryEvent.Mutate(it, false))
            }

        }

        setOnLongClickListener {
            memory?.let {
                eventListener?.invoke(MemoryEvent.Remove(it))
            }
            true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val type = (memory?.state as? MemoryState.Bright)?.type
        when (type) {
            MemoryType.Triangle -> canvas.drawPath(configurePath(width.toFloat(), path), paint)
            MemoryType.Circle -> canvas.drawCircle(width/2f, height/2f, width/2f, paint)
            MemoryType.Square -> {
                if (rect == null)
                    rect = Rect(0, 0, width, height)
                rect?.let {
                    canvas.drawRect(it, paint)
                }
            }
        }
    }

    fun update(memory: Memory) {
        this.memory = memory
        if (memory.state is MemoryState.Faded) {
            visibility = GONE
        } else {
            visibility = VISIBLE
            invalidate()
        }
    }

    fun getHeight(width: Double): Float {
        return Math.sqrt((Math.pow(width, 2.0) - Math.pow((width / 2), 2.0))).toFloat()
    }

    fun configurePath(sideLength: Float, path: Path): Path {
        path.moveTo(0f, sideLength)
        path.lineTo(sideLength / 2f, sideLength - getHeight(sideLength.toDouble()))
        path.lineTo(sideLength, sideLength)

        return path
    }
}