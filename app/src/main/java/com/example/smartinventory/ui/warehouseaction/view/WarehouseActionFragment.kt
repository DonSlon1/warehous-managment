package com.example.smartinventory.ui.warehouseaction.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartinventory.R
import com.example.smartinventory.databinding.FragmentWarehouseActionBinding
import com.example.smartinventory.ui.warehouseaction.adapter.WarehouseActionAdapter
import com.example.smartinventory.viewmodel.warehouseaction.WarehouseActionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WarehouseActionFragment: Fragment() {
    private var _binding: FragmentWarehouseActionBinding? = null
    private val binding get() = _binding!!

    private lateinit var warehouseActionAdapter: WarehouseActionAdapter
    private lateinit var warehouseActionViewModel: WarehouseActionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWarehouseActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        warehouseActionViewModel = ViewModelProvider(this).get(WarehouseActionViewModel::class.java)
        warehouseActionAdapter = WarehouseActionAdapter { inventoryItem ->
            // Navigate to AddItemFragment with the selected item for editing
/*
            val action = MainFragmentDirections.actionMainFragmentToAddItemFragment(inventoryItem)
            findNavController().navigate(action)
*/
        }

        binding.recyclerView.apply {
            adapter = warehouseActionAdapter
            layoutManager = LinearLayoutManager(context)
        }

        warehouseActionViewModel.allActionItems.observe(viewLifecycleOwner) { items ->
            warehouseActionAdapter.submitList(items)
        }

        binding.fabAddItem.setOnClickListener {
            // Navigate to AddItemFragment without any item for adding a new item
            findNavController().navigate(R.id.action_warehouseActionFragment_to_navAddWarehouseItemFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}