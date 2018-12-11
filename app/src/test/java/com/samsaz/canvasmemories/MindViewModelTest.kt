package com.samsaz.canvasmemories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.samsaz.canvasmemories.mind.MindViewModel
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryState
import com.samsaz.canvasmemories.model.MemoryType
import com.samsaz.canvasmemories.shared.LiveDataTestUtil
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindViewModelTest {

    val mem = Memory(1, 0, 0, MemoryState.Bright(MemoryType.Circle))

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    fun getLastMemory(viewModel: MindViewModel): Memory? {
        val seq = LiveDataTestUtil.getValue(viewModel.getMemoriesLiveData())
        return seq?.last()
    }

    fun getSequence(viewModel: MindViewModel): Sequence<Memory>? {
        return LiveDataTestUtil.getValue(viewModel.getMemoriesLiveData())
    }

    @Test
    fun addMemoryTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        val newMem = getLastMemory(vm)
        assertEquals(m, newMem)
    }

    @Test
    fun mutateMemoryTest() {
        var m = mem.copy(state = MemoryState.Bright(MemoryType.Circle))
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))

        // Circle -> Triangle
        vm.onMemoryEvent(MemoryEvent.Mutate(m, false))
        m = getLastMemory(vm)!!
        var type = (m.state as MemoryState.Bright).type
        assertEquals(MemoryType.Triangle, type)

        // Triangle -> Square
        vm.onMemoryEvent(MemoryEvent.Mutate(m, false))
        m = getLastMemory(vm)!!
        type = (m.state as MemoryState.Bright).type
        assertEquals(MemoryType.Square, type)

        // Square -> Circle
        vm.onMemoryEvent(MemoryEvent.Mutate(m, false))
        m = getLastMemory(vm)!!
        type = (m.state as MemoryState.Bright).type
        assertEquals(MemoryType.Circle, type)
    }

    @Test
    fun forgetMemoryTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.onMemoryEvent(MemoryEvent.Forget(m))
        val newMem = getLastMemory(vm)
        assertEquals(MemoryState.Faded, newMem?.state)
    }

    @Test
    fun rememberMemoryTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.onMemoryEvent(MemoryEvent.Forget(m))
        vm.onMemoryEvent(MemoryEvent.Remember(listOf(m)))
        val newMem = getLastMemory(vm)!!
        assertEquals(m.state, newMem.state)
    }

    @Test
    fun eraseMemoryTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.onMemoryEvent(MemoryEvent.Erase(m))
        val newMem = getLastMemory(vm)
        assertEquals(MemoryState.Erased, newMem?.state)
    }

    // Beware: This test title is a bit scary
    @Test
    fun erasedMemoryCannotBeRememberedTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.onMemoryEvent(MemoryEvent.Erase(m))
        vm.onMemoryEvent(MemoryEvent.Remember(m))
        val newMem = getLastMemory(vm)
        assertEquals(MemoryState.Erased, newMem?.state)
    }

    @Test
    fun undoAddTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.undo()
        val newMem = getLastMemory(vm)
        assertEquals(MemoryState.Erased, newMem?.state)
    }

    @Test
    fun undoMutateTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.onMemoryEvent(MemoryEvent.Mutate(m, false))
        vm.undo()
        val newMem = getLastMemory(vm)
        assertEquals(newMem, m)
    }

    @Test
    fun undoForgetTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        vm.onMemoryEvent(MemoryEvent.Add(m))
        vm.onMemoryEvent(MemoryEvent.Forget(m))
        vm.undo()
        val newMem = getLastMemory(vm)
        assertEquals(newMem, m)
    }

    @Test
    fun undoNothingTest() {
        val vm = MindViewModel()
        vm.undo()
        val seq = getSequence(vm)
        assertEquals(null, seq?.firstOrNull())
    }

    @Test
    fun undoEnabledTest() {
        val m = mem.copy()
        val vm = MindViewModel()

        var enabled = LiveDataTestUtil.getValue(vm.getUndoEnabledLiveData())
        assertEquals(false, enabled)

        vm.onMemoryEvent(MemoryEvent.Add(m))
        enabled = LiveDataTestUtil.getValue(vm.getUndoEnabledLiveData())
        assertEquals(true, enabled)

        vm.undo()
        enabled = LiveDataTestUtil.getValue(vm.getUndoEnabledLiveData())
        assertEquals(false, enabled)
    }

}