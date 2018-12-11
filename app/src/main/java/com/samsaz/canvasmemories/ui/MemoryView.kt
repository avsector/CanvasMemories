package com.samsaz.canvasmemories.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryState
import com.samsaz.canvasmemories.model.MemoryType

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */
class MemoryView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                           var memory: Memory? = null): View(context, attrs){

    private val defaultType: MemoryType
    private val drawers by lazy(LazyThreadSafetyMode.NONE) {
        mapOf(MemoryType.Square to SquareDrawer(width), MemoryType.Circle to CircleDrawer(width),
            MemoryType.Triangle to TriangleDrawer(width))
    }
    var eventListener: ((MemoryEvent) -> Unit)? = null

    init {
        setOnClickListener {
            memory?.let {
                eventListener?.invoke(MemoryEvent.Mutate(it, false))
            }
        }

        setOnLongClickListener {
            memory?.let { memory ->
                eventListener?.invoke(MemoryEvent.Remove(memory))
            }
            true
        }

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MemoryView)
            val type = a.getString(R.styleable.MemoryView_memoryType)
            defaultType = when (type?.toLowerCase()) {
                "square" -> MemoryType.Square
                "triangle" -> MemoryType.Triangle
                "circle" -> MemoryType.Circle
                else -> MemoryType.None
            }
            a.recycle()
        } else {
            defaultType = MemoryType.None
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val type = (memory?.state as? MemoryState.Bright)?.type ?: defaultType
        drawers[type]?.draw(canvas)
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
}