package com.samsaz.canvasmemories.model

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

sealed class MemoryState {
    data class Bright(val type: MemoryType): MemoryState()
    object Faded: MemoryState()
    object Erased: MemoryState()
}

sealed class MemoryType {
    object Square: MemoryType()
    object Circle: MemoryType()
    object Triangle: MemoryType()
    object None: MemoryType()
}