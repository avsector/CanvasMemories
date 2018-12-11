package com.samsaz.canvasmemories.mind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryState
import com.samsaz.canvasmemories.model.MemoryType
import com.samsaz.canvasmemories.util.iterator
import kotlinx.android.synthetic.main.fragment_mind_stats.view.*

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindStatsFragment: Fragment() {

    lateinit var viewModel: MindViewModel
    lateinit var groups: Map<MemoryType?, List<Memory>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity())[MindViewModel::class.java]
        groups = viewModel.memoryList.iterator().asSequence().groupBy {
            val state = it.state
            if (state is MemoryState.Bright) {
                state.type
            } else {
                null
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mind_stats, container, false)
        with(view) {
            tvCircles.text = getString(R.string.circles,
                groups[MemoryType.Circle]?.size ?: 0)
            tvSquares.text = getString(R.string.squares,
                groups[MemoryType.Square]?.size ?: 0)
            tvTriangles.text = getString(R.string.triangles,
                groups[MemoryType.Triangle]?.size ?: 0)
        }
        return view
    }
}