package com.samsaz.canvasmemories.mind

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samsaz.canvasmemories.R
import com.samsaz.canvasmemories.model.Memory
import com.samsaz.canvasmemories.model.MemoryEvent
import com.samsaz.canvasmemories.model.MemoryState
import com.samsaz.canvasmemories.model.MemoryType
import kotlinx.android.synthetic.main.item_memory_type.view.*

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class MemoryTypeAdapter: RecyclerView.Adapter<MemoryTypeAdapter.ViewHolder>() {
    var items: List<Pair<MemoryType, List<Memory>>> = emptyList()
    var memoryEventListener: ((MemoryEvent) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memory_type, parent,
            false)
        val viewHolder = ViewHolder(view)
        view.btnDeleteAll.setOnClickListener {
            val position = viewHolder.adapterPosition
            val list = items[position].second
            items = items.filterIndexed { index, _ ->
                index != position
            }
            notifyDataSetChanged()
            memoryEventListener?.invoke(MemoryEvent.Forget(list))
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val square = Memory(0, 0, 0, MemoryState.Bright(MemoryType.Square))
        private val triangle = Memory(0, 0, 0, MemoryState.Bright(MemoryType.Triangle))
        private val circle = Memory(0, 0, 0, MemoryState.Bright(MemoryType.Circle))

        fun bind(item: Pair<MemoryType, List<Memory>>) {
            val size = item.second.size

            val memory: Memory
            val titleResId: Int

            when(item.first) {
                is MemoryType.Square -> {
                    memory = square
                    titleResId = R.string.squares
                }
                is MemoryType.Triangle -> {
                    memory = triangle
                    titleResId = R.string.triangles
                }
                is MemoryType.Circle -> {
                    memory = circle
                    titleResId = R.string.circles
                }
                else -> return
            }

            itemView.memoryView.update(memory)
            itemView.tvTitle.text = itemView.resources.getString(titleResId, size)

            itemView.btnDeleteAll.visibility = if (size > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}