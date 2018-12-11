package com.samsaz.canvasmemories.model

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */
sealed class MemoryEvent(val memory: Memory) {
    class Add(memory: Memory): MemoryEvent(memory)
    class Remove(memory: Memory): MemoryEvent(memory)
    class Mutate(memory: Memory, val reverse: Boolean): MemoryEvent(memory)
    class UnRemove(memory: Memory): MemoryEvent(memory)
}