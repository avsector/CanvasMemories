package com.samsaz.canvasmemories.mind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Screen
import kotlinx.android.synthetic.main.fragment_mind_stats.*
import kotlinx.android.synthetic.main.fragment_mind_stats.view.*

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MindStatsFragment: Fragment() {

    private lateinit var viewModel: MindViewModel
    private val adapter = MemoryTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity())[MindViewModel::class.java]
        adapter.items = viewModel.getMemoriesGroupedByType()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mind_stats, container, false)
        setupView(view)
        return view
    }

    private fun setupView(view: View) = with(view) {
        rvList.layoutManager = LinearLayoutManager(context)
        rvList.adapter = adapter
        btnEditor.setOnClickListener {
            viewModel.navigate(to = Screen.Editor)
        }
        tvBlankMind.visibility = if (adapter.items.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}