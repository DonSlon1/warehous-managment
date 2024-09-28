package com.example.smartinventory.ui.warehouseaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.databinding.ItemWarehouseActionBinding

class WarehouseActionAdapter(private val onEditClick: (WarehouseAction) -> Unit) :
    ListAdapter<WarehouseAction, WarehouseActionAdapter.WarehouseItemViewHolder>(WarehouseItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseItemViewHolder {
        val binding = ItemWarehouseActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WarehouseItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WarehouseItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class WarehouseItemViewHolder(private val binding: ItemWarehouseActionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WarehouseAction) {
            binding.inventoryItem = item
            binding.executePendingBindings()

            // Handle edit button click
            binding.itemLayout.setOnClickListener {
                onEditClick(item)
            }

            // Optionally, handle item click for viewing/editing
            binding.root.setOnClickListener {
                onEditClick(item)
            }
        }
    }

    class WarehouseItemDiffCallback : DiffUtil.ItemCallback<WarehouseAction>() {
        override fun areItemsTheSame(oldItem: WarehouseAction, newItem: WarehouseAction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WarehouseAction, newItem: WarehouseAction): Boolean {
            return oldItem == newItem
        }
    }
}
