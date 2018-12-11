package com.samsaz.canvasmemories.mind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryType
import com.samsaz.canvasmemories.model.Screen
import com.samsaz.canvasmemories.ui.MemoryView
import kotlinx.android.synthetic.main.fragment_mind_editor.*
import kotlinx.android.synthetic.main.fragment_mind_editor.view.*

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindEditorFragment : Fragment() {

    private val viewList = SparseArrayCompat<MemoryView>()
    private lateinit var viewModel: MindViewModel
    private val viewEventListener = { event: MemoryEvent ->
        viewModel.onMemoryEvent(event)
    }
    private val frameDimensions by lazy(LazyThreadSafetyMode.NONE) {
        val bottomBarHeight = resources.getDimensionPixelSize(R.dimen.bottomBarHeight)
        val memorySize = resources.getDimensionPixelSize(R.dimen.memorySize)
        val w = resources.displayMetrics.widthPixels - memorySize
        val h = frame.measuredHeight - bottomBarHeight - memorySize
        w to h
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity())[MindViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_mind_editor, container, false)
        setupView(view)
        viewModel.created()

        return view
    }

    private fun setupView(view: View) = with(view) {
        val addMemoryClickListener = { view: View ->
            val type = when(view.id) {
                R.id.addCircleMemoryView -> MemoryType.Circle
                R.id.addSquareMemoryView -> MemoryType.Square
                R.id.addTriangleMemoryView -> MemoryType.Triangle
                else -> MemoryType.None
            }
            viewModel.newMemory(type, frameDimensions)
        }
        addCircleMemoryView.setOnClickListener(addMemoryClickListener)
        addSquareMemoryView.setOnClickListener(addMemoryClickListener)
        addTriangleMemoryView.setOnClickListener(addMemoryClickListener)

        view.undo.setOnClickListener {
            viewModel.undo()
        }
        view.stats.setOnClickListener {
            viewModel.navigate(to = Screen.Stats)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getMemoriesLiveData().observe(this, Observer {
            it?.forEach { memory ->
                updateMemoryState(memory)
            }
        })
    }

    private fun addMemoryView(memory: Memory) {
        val memorySize = resources.getDimensionPixelSize(R.dimen.memorySize)
        val lp = FrameLayout.LayoutParams(memorySize, memorySize)
        lp.leftMargin = memory.x
        lp.topMargin = memory.y
        val view = MemoryView(requireContext(), memory = memory)
        view.layoutParams = lp
        frame.addView(view)
        viewList.put(memory.id, view)
        view.eventListener = viewEventListener
    }

    private fun updateMemoryState(memory: Memory) {
        val view = viewList[memory.id]
        if (view != null)
            view.update(memory)
        else
            addMemoryView(memory)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewList.clear()
    }

}