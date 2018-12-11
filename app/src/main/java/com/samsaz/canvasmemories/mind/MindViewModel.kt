package com.samsaz.canvasmemories.mind

import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.samsaz.canvasmemories.model.*
import com.samsaz.canvasmemories.util.SingleLiveEvent
import com.samsaz.canvasmemories.util.iterator
import java.util.*
import kotlin.random.Random

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindViewModel : ViewModel() {
    private val memoryList = SparseArrayCompat<Memory>()
    private val eventStack = Stack<MemoryEvent>()
    private var identifier: Int = 0
    private val memoriesLiveData = SingleLiveEvent<Sequence<Memory>>().apply {
        value = emptySequence()
    }
    private val screenLiveData = MutableLiveData<Screen>().apply {
        value = Screen.Editor
    }
    private val undoEnabledLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun onMemoryEvent(event: MemoryEvent, fromUndo: Boolean = false) {
        when (event) {
            is MemoryEvent.Add -> {
                addNewMemory(event.memory)
            }
            is MemoryEvent.Forget -> {
                updateMemoryState(event.memories, MemoryState.Faded)
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
            is MemoryEvent.Remember -> {
                if (event.memories.isEmpty())
                    return
                val newState = event.memories[0].state
                updateMemoryState(event.memories, newState)
            }
            is MemoryEvent.Erase -> {
                updateMemoryState(event.memory, MemoryState.Erased)
            }
        }

        if (!fromUndo)
            addToEventStack(event)
    }

    fun undo() {
        val event = popFromEventStack()
        val undoEvent = when (event) {
            is MemoryEvent.Add -> MemoryEvent.Erase(event.memory)
            is MemoryEvent.Forget -> MemoryEvent.Remember(event.memories)
            is MemoryEvent.Mutate -> MemoryEvent.Mutate(event.memory, true)
            else -> null
        }

        if (undoEvent != null) {
            onMemoryEvent(undoEvent, true)
        }
    }

    fun navigate(to: Screen) {
        screenLiveData.value = to
    }

    fun newMemory(type: MemoryType, frameDimension: Pair<Int, Int>) {
        val (x, y) = getMemoryViewPosition(frameDimension)
        onMemoryEvent(MemoryEvent.Add(Memory(++identifier, x, y, MemoryState.Bright(type))))
    }

    fun created() {
        memoriesLiveData.value = memoryList.iterator().asSequence()
    }

    fun getMemoriesLiveData(): LiveData<Sequence<Memory>> {
        return memoriesLiveData
    }

    fun getScreenLiveData(): LiveData<Screen> {
        return screenLiveData
    }

    fun getUndoEnabledLiveData(): LiveData<Boolean> {
        return undoEnabledLiveData
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

    private fun addNewMemory(memory: Memory) {
        memoryList.put(memory.id, memory)
        updateMemoriesLiveData(memory)
    }

    private fun nextMemoryType(state: MemoryType?): MemoryType? {
        when (state) {
            MemoryType.Circle -> return MemoryType.Triangle
            MemoryType.Triangle -> return MemoryType.Square
            MemoryType.Square -> return MemoryType.Circle
        }

        return null
    }

    private fun updateMemoryState(memory: Memory, newState: MemoryState) {
        val mem = memoryList[memory.id] ?: return

        val newMem = mem.copy(state = newState)
        if (newMem.state == MemoryState.Erased) {
            memoryList.remove(newMem.id)
        } else {
            memoryList.put(newMem.id, newMem)
        }
        updateMemoriesLiveData(newMem)
    }

    private fun updateMemoryState(memories: List<Memory>, newState: MemoryState) {
        val newList = memories.mapNotNull {
            val index = memoryList.indexOfKey(it.id)
            if (index < 0)
                null
            else {
                val newMem = it.copy(state = newState)
                memoryList.setValueAt(index, newMem)
                newMem
            }
        }
        updateMemoriesLiveData(newList.asSequence())
    }

    private fun updateMemoriesLiveData(memory: Memory) {
        updateMemoriesLiveData(sequenceOf(memory))
    }

    private fun updateMemoriesLiveData(sequence: Sequence<Memory>) {
        if (memoriesLiveData.hasActiveObservers()) {
            memoriesLiveData.value = sequence
        } else {
            memoriesLiveData.value = memoriesLiveData.value?.plus(sequence)
        }
    }

    private fun getMemoryViewPosition(frameDimension: Pair<Int, Int>): Pair<Int, Int> {
        val x = Random.nextInt(from = 0, until = frameDimension.first)
        val y = Random.nextInt(from = 0, until = frameDimension.second)
        return x to y
    }

    private fun addToEventStack(event: MemoryEvent) {
        eventStack.push(event)
        updateUndoLiveData()
    }

    private fun popFromEventStack(): MemoryEvent? {
        if (eventStack.isEmpty())
            return null

        val event = eventStack.pop()
        updateUndoLiveData()
        return event
    }

    private fun updateUndoLiveData() {
        undoEnabledLiveData.value = eventStack.isNotEmpty()
    }
}