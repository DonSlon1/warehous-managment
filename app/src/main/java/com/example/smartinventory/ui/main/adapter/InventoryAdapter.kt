package com.example.smartinventory.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.databinding.ItemInventoryBinding

class InventoryAdapter : ListAdapter<InventoryItem, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class InventoryViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItem) {
            binding.inventoryItem = item
            binding.executePendingBindings()
        }
    }

    class InventoryDiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
