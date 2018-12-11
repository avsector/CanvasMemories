package com.samsaz.canvasmemories.mind

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.samsaz.canvasmemories.ui.MemoryView
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryType
import kotlinx.android.synthetic.main.fragment_mind_canvas.*
import kotlinx.android.synthetic.main.fragment_mind_canvas.view.*

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindCanvasFragment : Fragment() {

    private val viewList = SparseArrayCompat<MemoryView>()
    private lateinit var viewModel: MindViewModel
    private val viewEventListener = { event: MemoryEvent ->
        viewModel.onMemoryEvent(event)
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

        val view = inflater.inflate(R.layout.fragment_mind_canvas, container, false)
        view.memoryButton.setOnClickListener {
            viewModel.newMemory(MemoryType.Circle, getFrameDimensions())
        }
        view.undo.setOnClickListener {
            viewModel.undo()
        }

        viewModel.created()
        return view
    }

    override fun onStart() {
        super.onStart()
        viewModel.memoriesLiveData.observe(this, Observer {
            it?.forEach {
                Log.d("SEQ", "Updated ${it.id}")
                updateMemoryState(it)
            }
        })
    }

    fun getFrameDimensions(): Pair<Int, Int> {
        val bottomBarHeight = resources.getDimensionPixelSize(R.dimen.bottomBarHeight)
        val memorySize = resources.getDimensionPixelSize(R.dimen.memorySize)
        val w = resources.displayMetrics.widthPixels - memorySize
        val h = frame.measuredHeight - bottomBarHeight - memorySize
        return w to h
    }

    fun addMemoryView(memory: Memory) {
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

    fun updateMemoryState(memory: Memory) {
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