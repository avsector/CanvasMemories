package com.samsaz.canvasmemories.model

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */
sealed class MemoryEvent {
    data class Add(val memory: Memory): MemoryEvent()
    data class Remove(val memories: List<Memory>): MemoryEvent() {
        constructor(memory: Memory) : this(listOf(memory))
    }
    data class Mutate(val memory: Memory, val reverse: Boolean): MemoryEvent()
    data class UnRemove(val memories: List<Memory>): MemoryEvent()
}