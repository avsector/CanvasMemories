package com.samsaz.canvasmemories.mind

import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryState
import com.samsaz.canvasmemories.model.MemoryType
import com.samsaz.canvasmemories.util.SingleLiveEvent
import com.samsaz.canvasmemories.util.iterator
import java.util.*
import kotlin.random.Random

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindViewModel: ViewModel() {
    private val memoryList = SparseArrayCompat<Memory>()
    private val eventStack = Stack<MemoryEvent>()
    private var identifier: Int = 0
    private val memoriesLiveData = SingleLiveEvent<Sequence<Memory>>().apply {
        value = emptySequence()
    }

    fun onMemoryEvent(event: MemoryEvent, fromUndo: Boolean = false) {
        when (event) {
            is MemoryEvent.Add -> {
                addNewMemory(event.memory)
            }
            is MemoryEvent.Remove -> {
                val mem = event.memory
                updateMemoryState(mem, MemoryState.Faded)
            }
            is MemoryEvent.Mutate -> {
                val type = (event.memory.state as? MemoryState.Bright)?.type
                val newType = if (event.reverse)
                    type
                else
                    nextMemoryType(type)

                if (newType != null) {
                    updateMemoryState(event.memory, MemoryState.Bright(newType))
                }
            }
            is MemoryEvent.UnRemove -> {
                val newState = event.memory.state
                updateMemoryState(event.memory, newState)
            }
        }

        if (!fromUndo)
            eventStack.push(event)
    }

    private fun addNewMemory(memory: Memory) {
        memoryList.put(memory.id, memory)
        updateLiveData(memory)
    }

    fun nextMemoryType(state: MemoryType?): MemoryType? {
        when(state) {
            MemoryType.Circle -> return MemoryType.Triangle
            MemoryType.Triangle -> return MemoryType.Square
            MemoryType.Square -> return MemoryType.Circle
        }

        return null
    }

    fun updateMemoryState(memory: Memory, newState: MemoryState) {
        val mem = memoryList[memory.id]
        mem ?: return

        val newMem = mem.copy(state = newState)
        memoryList.put(newMem.id, newMem)
        updateLiveData(newMem)
    }

    fun undo() {
        if (eventStack.isEmpty())
            return

        val event = eventStack.pop()
        val undoEvent = when (event) {
            is MemoryEvent.Add -> MemoryEvent.Remove(event.memory)
            is MemoryEvent.Remove -> MemoryEvent.UnRemove(event.memory)
            is MemoryEvent.Mutate -> MemoryEvent.Mutate(event.memory, true)
            else -> null
        }

        if (undoEvent != null) {
            onMemoryEvent(undoEvent, true)
        }

    }

    fun newMemory(type: MemoryType, frameDimension: Pair<Int, Int>) {
        val (x, y) = getMemoryViewPosition(frameDimension)
        onMemoryEvent(
            MemoryEvent.Add(
                Memory(
                    ++identifier,
                    x,
                    y,
                    MemoryState.Bright(type)
                )
            ))
    }

    fun created() {
        memoriesLiveData.value = memoryList.iterator().asSequence()
    }

    fun updateLiveData(memory: Memory) {
        if (memoriesLiveData.hasActiveObservers()) {
            memoriesLiveData.value = sequenceOf(memory)
        } else {
            memoriesLiveData.value = memoriesLiveData.value?.plus(memory)
        }
    }

    fun getMemoryViewPosition(frameDimension: Pair<Int, Int>): Pair<Int, Int> {
        val x = Random.nextInt(from = 0, until = frameDimension.first)
        val y = Random.nextInt(from = 0, until = frameDimension.second)
        return x to y
    }

    fun getMemoriesLiveData(): LiveData<Sequence<Memory>> {
        return memoriesLiveData
    }

    fun getMemoriesGroupedByType(): List<Pair<MemoryType, List<Memory>>> {
        return memoryList.iterator().asSequence().groupBy {
            val state = it.state
            if (state is MemoryState.Bright) {
                state.type
            } else {
                MemoryType.None
            }
        }.filter { it.key !is MemoryType.None }.toList()
    }
}